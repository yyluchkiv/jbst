package jbst.foundation.utils;

import com.blueconic.browscap.BrowsCapField;
import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import feign.Feign;
import feign.Param;
import feign.RequestLine;
import feign.Retryer;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.enums.JbstStatus;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.geo.JbstGeoCountryFlag;
import jbst.foundation.domain.geo.JbstGeoLocation;
import jbst.foundation.domain.base.IPAddress;
import jbst.foundation.domain.http.requests.JbstUserAgentDetails;
import jbst.foundation.domain.http.requests.JbstUserAgentHeader;
import jbst.foundation.domain.http.requests.JbstUserRequestMetadata;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.configs.utilities.JbstPropertyCountriesFlags;
import jbst.foundation.domain.properties.configs.utilities.JbstPropertyGeolocations;
import jbst.foundation.domain.properties.configs.utilities.JbstPropertyUsersAgents;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.isNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static jbst.foundation.domain.constants.JbstConstants.Logs.PREFIX;
import static jbst.foundation.domain.enums.JbstStatus.FAILURE;
import static jbst.foundation.domain.enums.JbstStatus.SUCCESS;
import static jbst.foundation.domain.strings.JbstMessages.contactDevelopmentTeam;

@Slf4j
@Component
public class JbstGeoUtils {
    private static final String CONFIGURATION_LOG_FLAGS = PREFIX + " geo country flags geo-countries-flags.json — {}";
    private static final String CONFIGURATION_LOG_MINDMAX = PREFIX + " geo location database GeoLite2-City.mmdb — {}";
    private static final String CONFIGURATION_LOG_USER_AGENT_DETAILS = PREFIX + " user agent — {}";

    // ================================================================================================================
    // CLASSES: FLAGS
    // ================================================================================================================
    private record GeoFlags(
            JbstPropertyCountriesFlags configs,
            Map<String, JbstGeoCountryFlag> names,
            Map<String, JbstGeoCountryFlag> codes
    ) {
        public String getEmojiByName(String searchKey) {
            return this.getEmoji(this.names, searchKey);
        }

        public String getEmojiByCode(String searchKey) {
            return this.getEmoji(this.codes, searchKey);
        }
        // ================================================================================================================
        // PRIVATE METHODS
        // ================================================================================================================
        public String getEmoji(Map<String, JbstGeoCountryFlag> mappedBy, String searchKey) {
            if (!this.configs.isEnabled()) {
                return JbstGeoCountryFlag.unknown().emoji();
            }
            if (isNull(searchKey)) {
                searchKey = JbstConstants.Strings.UNKNOWN.toLowerCase();
            }
            return mappedBy.getOrDefault(searchKey.toLowerCase(), JbstGeoCountryFlag.unknown()).emoji();
        }
    }

    // ================================================================================================================
    // CLASSES: IPAPI
    // ================================================================================================================
    public interface IPAPIDefinition {
        @RequestLine("GET /json/{ipAddress}")
        IPAPIResponse getIPAPIResponse(@Param("ipAddress") String ipAddress);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record IPAPIResponse(String status, String country, String countryCode, String city, String message) {
        @JsonIgnore
        public boolean isSuccess() {
            return "success".equals(this.status);
        }
    }

    // ================================================================================================================
    // CLASSES: MINDMAX
    // ================================================================================================================
    private record GeoMindMax(JbstPropertyGeolocations configs, DatabaseReader databaseReader) {}

    // ================================================================================================================
    // CLASSES: User Agent Details
    // ================================================================================================================
    private record GeoUserAgentDetails(JbstPropertyUsersAgents configs, boolean configured, UserAgentParser userAgentParser, String exception) {
        public JbstUserAgentDetails getUserAgentDetails(JbstUserAgentHeader userAgentHeader) {
            if (!configs.isEnabled() || !this.configured) {
                return JbstUserAgentDetails.unknown(this.exception);
            }
            var capabilities = this.userAgentParser.parse(userAgentHeader.getValue());
            return JbstUserAgentDetails.processed(
                    capabilities.getBrowser(),
                    capabilities.getPlatform(),
                    capabilities.getDeviceType()
            );
        }
    }

    // Definitions
    private final IPAPIDefinition ipapi = Feign.builder()
            .client(new OkHttpClient())
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .retryer(Retryer.NEVER_RETRY)
            .target(IPAPIDefinition.class, "http://ip-api.com");
    // Properties
    private final JbstProperties jbstProperties;
    // State
    private final GeoFlags geoFlags;
    private final GeoMindMax geoMindMax;
    private final GeoUserAgentDetails geoUserAgentDetails;

    @Autowired
    public JbstGeoUtils(ResourceLoader resourceLoader, JbstProperties jbstProperties) {
        this.jbstProperties = jbstProperties;
        this.geoFlags = this.initFlags(resourceLoader);
        this.geoMindMax = this.initMindMax(resourceLoader);
        this.geoUserAgentDetails = this.initUserAgentDetails();
    }

    // ================================================================================================================
    // METHODS: FACADE
    // ================================================================================================================
    public final JbstGeoLocation getGeoLocation(IPAddress ipAddress) {
        try {
            return this.getGeoLocationIPAPI(ipAddress);
        } catch (JbstExceptions.GeoLocationNotFound ex1) {
            try {
                return this.getGeoLocationMindMax(ipAddress);
            } catch (JbstExceptions.GeoLocationNotFound ex2) {
                return JbstGeoLocation.unknown(ipAddress, ex2.getMessage());
            }
        }
    }

    public final JbstUserRequestMetadata getUserRequestMetadataProcessed(IPAddress ipAddress, JbstUserAgentHeader userAgentHeader) {
        return JbstUserRequestMetadata.processed(
                this.getGeoLocation(ipAddress),
                this.getUserAgentDetails(userAgentHeader)
        );
    }

    // ================================================================================================================
    // METHODS (atomic): FLAGS
    // ================================================================================================================
    public final String getFlagEmojiByCountryName(String countryName) {
        return this.geoFlags.getEmojiByName(countryName);
    }

    public final String getFlagEmojiByCountryCode(String countryCode) {
        return this.geoFlags.getEmojiByCode(countryCode);
    }

    // ================================================================================================================
    // METHODS (atomic): IPAPI
    // ================================================================================================================
    protected final JbstGeoLocation getGeoLocationIPAPI(IPAddress ipAddress) throws JbstExceptions.GeoLocationNotFound {
        try {
            var queryResponse = this.ipapi.getIPAPIResponse(ipAddress.value());
            if (queryResponse.isSuccess()) {
                var countryCode = queryResponse.countryCode();
                var countryFlag = this.getFlagEmojiByCountryCode(countryCode);
                return JbstGeoLocation.processed(
                        ipAddress,
                        queryResponse.country(),
                        countryCode,
                        countryFlag,
                        queryResponse.city()
                );
            } else {
                throw new JbstExceptions.GeoLocationNotFound(queryResponse.message());
            }
        } catch (RuntimeException throwable) {
            throw new JbstExceptions.GeoLocationNotFound(throwable.getMessage());
        }
    }

    // ================================================================================================================
    // METHODS (atomic): MINDMAX
    // ================================================================================================================
    protected final JbstGeoLocation getGeoLocationMindMax(IPAddress ipAddress) throws JbstExceptions.GeoLocationNotFound {
        if (!this.jbstProperties.getUtils().getGeolocations().isEnabled()) {
            return JbstGeoLocation.unknown(ipAddress, contactDevelopmentTeam("Geo configurations failure"));
        }
        try {
            var response = this.geoMindMax.databaseReader().city(InetAddress.getByName(ipAddress.value()));
            var countryCode = response.getCountry().getIsoCode();
            return JbstGeoLocation.processed(
                    ipAddress,
                    response.getCountry().getName(),
                    countryCode,
                    this.getFlagEmojiByCountryCode(countryCode),
                    response.getCity().getName()
            );
        } catch (IOException | GeoIp2Exception ex) {
            throw new JbstExceptions.GeoLocationNotFound(ex.getMessage());
        }
    }

    // ================================================================================================================
    // METHODS (atomic): MINDMAX
    // ================================================================================================================
    public final JbstUserAgentDetails getUserAgentDetails(JbstUserAgentHeader userAgentHeader) {
        return this.geoUserAgentDetails.getUserAgentDetails(userAgentHeader);
    }

    // ================================================================================================================
    // INITIALIZERS: FLAGS
    // ================================================================================================================
    private GeoFlags initFlags(ResourceLoader resourceLoader) {
        var enabled = this.jbstProperties.getUtils().getCountriesFlags().isEnabled();
        LOGGER.info(CONFIGURATION_LOG_FLAGS, JbstStatus.of(enabled).asANSI());
        if (enabled) {
            try {
                var resource = resourceLoader.getResource("classpath:geo-countries-flags.json");
                var typeReference = new TypeReference<List<JbstGeoCountryFlag>>() {};
                var objectMapper = new ObjectMapper();
                var geoCountryFlags = objectMapper.readValue(resource.getInputStream(), typeReference);
                LOGGER.info(CONFIGURATION_LOG_FLAGS, SUCCESS.asANSI());
                return new GeoFlags(
                        this.jbstProperties.getUtils().getCountriesFlags(),
                        geoCountryFlags.stream().collect(toUnmodifiableMap(item -> item.name().toLowerCase(), identity())),
                        geoCountryFlags.stream().collect(toUnmodifiableMap(item -> item.code().toLowerCase(), identity()))
                );
            } catch (IOException | RuntimeException ex) {
                LOGGER.error(CONFIGURATION_LOG_FLAGS, FAILURE.asANSI());
                LOGGER.error("Please make sure geo-countries-flags.json is in classpath");
                throw new IllegalArgumentException(ex.getMessage());
            }
        } else {
            return new GeoFlags(
                    this.jbstProperties.getUtils().getCountriesFlags(),
                    unmodifiableMap(new HashMap<>()),
                    unmodifiableMap(new HashMap<>())
            );
        }
    }

    // ================================================================================================================
    // INITIALIZERS: MINDMAX
    // ================================================================================================================
    private GeoMindMax initMindMax(ResourceLoader resourceLoader) {
        var enabled = this.jbstProperties.getUtils().getGeolocations().isEnabled();
        LOGGER.info(CONFIGURATION_LOG_MINDMAX, JbstStatus.of(enabled).asANSI());
        if (enabled) {
            try {
                var resource = resourceLoader.getResource("classpath:GeoLite2-City.mmdb");
                var inputStream = resource.getInputStream();
                LOGGER.info(CONFIGURATION_LOG_MINDMAX, SUCCESS.asANSI());
                return new GeoMindMax(
                        this.jbstProperties.getUtils().getGeolocations(),
                        new DatabaseReader.Builder(inputStream).build()
                );
            } catch (IOException | RuntimeException ex) {
                LOGGER.error(CONFIGURATION_LOG_MINDMAX, FAILURE.asANSI());
                LOGGER.error("Please make sure GeoLite2-City.mmdb is in classpath");
                throw new IllegalArgumentException(ex.getMessage());
            }
        } else {
            return new GeoMindMax(
                    this.jbstProperties.getUtils().getGeolocations(),
                    null
            );
        }
    }

    // ================================================================================================================
    // INITIALIZERS: User Agent Details
    // ================================================================================================================
    private GeoUserAgentDetails initUserAgentDetails() {
        var enabled = this.jbstProperties.getUtils().getUsersAgents().isEnabled();
        LOGGER.info(CONFIGURATION_LOG_USER_AGENT_DETAILS, JbstStatus.of(enabled).asANSI());
        if (enabled) {
            try {
                LOGGER.info(CONFIGURATION_LOG_USER_AGENT_DETAILS, SUCCESS);
                return new GeoUserAgentDetails(
                        this.jbstProperties.getUtils().getUsersAgents(),
                        true,
                        new UserAgentService().loadParser(
                                List.of(
                                        BrowsCapField.BROWSER,
                                        BrowsCapField.PLATFORM,
                                        BrowsCapField.DEVICE_TYPE
                                )
                        ),
                        null
                );
            } catch (ParseException | IOException ex) {
                LOGGER.error(CONFIGURATION_LOG_USER_AGENT_DETAILS, FAILURE);
                throw new IllegalArgumentException(ex);
            }
        } else {
            return new GeoUserAgentDetails(
                    this.jbstProperties.getUtils().getUsersAgents(),
                    false,
                    null,
                    contactDevelopmentTeam("User agent configuration failure")
            );
        }
    }
}
