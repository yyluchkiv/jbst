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
import jbst.foundation.domain.enums.Status;
import jbst.foundation.domain.exceptions.geo.JbstGeoLocationNotFoundException;
import jbst.foundation.domain.geo.GeoCountryFlag;
import jbst.foundation.domain.geo.GeoLocation;
import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.domain.http.requests.UserAgentDetails;
import jbst.foundation.domain.http.requests.UserAgentHeader;
import jbst.foundation.domain.http.requests.UserRequestMetadata;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.configs.utilities.GeoCountryFlagsConfigs;
import jbst.foundation.domain.properties.configs.utilities.GeoLocationsConfigs;
import jbst.foundation.domain.properties.configs.utilities.UserAgentConfigs;
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
import static jbst.foundation.domain.enums.Status.FAILURE;
import static jbst.foundation.domain.enums.Status.SUCCESS;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.contactDevelopmentTeam;

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
            GeoCountryFlagsConfigs configs,
            Map<String, GeoCountryFlag> names,
            Map<String, GeoCountryFlag> codes
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
        public String getEmoji(Map<String, GeoCountryFlag> mappedBy, String searchKey) {
            if (!this.configs.isEnabled()) {
                return GeoCountryFlag.unknown().emoji();
            }
            if (isNull(searchKey)) {
                searchKey = JbstConstants.Strings.UNKNOWN.toLowerCase();
            }
            return mappedBy.getOrDefault(searchKey.toLowerCase(), GeoCountryFlag.unknown()).emoji();
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
    private record GeoMindMax(GeoLocationsConfigs configs, DatabaseReader databaseReader) {}

    // ================================================================================================================
    // CLASSES: User Agent Details
    // ================================================================================================================
    private record GeoUserAgentDetails(UserAgentConfigs configs, boolean configured, UserAgentParser userAgentParser, String exception) {
        public UserAgentDetails getUserAgentDetails(UserAgentHeader userAgentHeader) {
            if (!configs.isEnabled() || !this.configured) {
                return UserAgentDetails.unknown(this.exception);
            }
            var capabilities = this.userAgentParser.parse(userAgentHeader.getValue());
            return UserAgentDetails.processed(
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
    public final GeoLocation getGeoLocation(IPAddress ipAddress) {
        try {
            return this.getGeoLocationIPAPI(ipAddress);
        } catch (JbstGeoLocationNotFoundException ex1) {
            try {
                return this.getGeoLocationMindMax(ipAddress);
            } catch (JbstGeoLocationNotFoundException ex2) {
                return GeoLocation.unknown(ipAddress, ex2.getMessage());
            }
        }
    }

    public final UserRequestMetadata getUserRequestMetadataProcessed(IPAddress ipAddress, UserAgentHeader userAgentHeader) {
        return UserRequestMetadata.processed(
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
    protected final GeoLocation getGeoLocationIPAPI(IPAddress ipAddress) throws JbstGeoLocationNotFoundException {
        try {
            var queryResponse = this.ipapi.getIPAPIResponse(ipAddress.value());
            if (queryResponse.isSuccess()) {
                var countryCode = queryResponse.countryCode();
                var countryFlag = this.getFlagEmojiByCountryCode(countryCode);
                return GeoLocation.processed(
                        ipAddress,
                        queryResponse.country(),
                        countryCode,
                        countryFlag,
                        queryResponse.city()
                );
            } else {
                throw new JbstGeoLocationNotFoundException(queryResponse.message());
            }
        } catch (RuntimeException throwable) {
            throw new JbstGeoLocationNotFoundException(throwable.getMessage());
        }
    }

    // ================================================================================================================
    // METHODS (atomic): MINDMAX
    // ================================================================================================================
    protected final GeoLocation getGeoLocationMindMax(IPAddress ipAddress) throws JbstGeoLocationNotFoundException {
        if (!this.jbstProperties.getUtilsConfigs().getGeoLocationsConfigs().isEnabled()) {
            return GeoLocation.unknown(ipAddress, contactDevelopmentTeam("Geo configurations failure"));
        }
        try {
            var response = this.geoMindMax.databaseReader().city(InetAddress.getByName(ipAddress.value()));
            var countryCode = response.getCountry().getIsoCode();
            return GeoLocation.processed(
                    ipAddress,
                    response.getCountry().getName(),
                    countryCode,
                    this.getFlagEmojiByCountryCode(countryCode),
                    response.getCity().getName()
            );
        } catch (IOException | GeoIp2Exception ex) {
            throw new JbstGeoLocationNotFoundException(ex.getMessage());
        }
    }

    // ================================================================================================================
    // METHODS (atomic): MINDMAX
    // ================================================================================================================
    public final UserAgentDetails getUserAgentDetails(UserAgentHeader userAgentHeader) {
        return this.geoUserAgentDetails.getUserAgentDetails(userAgentHeader);
    }

    // ================================================================================================================
    // INITIALIZERS: FLAGS
    // ================================================================================================================
    private GeoFlags initFlags(ResourceLoader resourceLoader) {
        var enabled = this.jbstProperties.getUtilsConfigs().getGeoCountryFlagsConfigs().isEnabled();
        LOGGER.info(CONFIGURATION_LOG_FLAGS, Status.of(enabled).asANSI());
        if (enabled) {
            try {
                var resource = resourceLoader.getResource("classpath:geo-countries-flags.json");
                var typeReference = new TypeReference<List<GeoCountryFlag>>() {};
                var objectMapper = new ObjectMapper();
                var geoCountryFlags = objectMapper.readValue(resource.getInputStream(), typeReference);
                LOGGER.info(CONFIGURATION_LOG_FLAGS, SUCCESS.asANSI());
                return new GeoFlags(
                        this.jbstProperties.getUtilsConfigs().getGeoCountryFlagsConfigs(),
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
                    this.jbstProperties.getUtilsConfigs().getGeoCountryFlagsConfigs(),
                    unmodifiableMap(new HashMap<>()),
                    unmodifiableMap(new HashMap<>())
            );
        }
    }

    // ================================================================================================================
    // INITIALIZERS: MINDMAX
    // ================================================================================================================
    private GeoMindMax initMindMax(ResourceLoader resourceLoader) {
        var enabled = this.jbstProperties.getUtilsConfigs().getGeoLocationsConfigs().isEnabled();
        LOGGER.info(CONFIGURATION_LOG_MINDMAX, Status.of(enabled).asANSI());
        if (enabled) {
            try {
                var resource = resourceLoader.getResource("classpath:GeoLite2-City.mmdb");
                var inputStream = resource.getInputStream();
                LOGGER.info(CONFIGURATION_LOG_MINDMAX, SUCCESS.asANSI());
                return new GeoMindMax(
                        this.jbstProperties.getUtilsConfigs().getGeoLocationsConfigs(),
                        new DatabaseReader.Builder(inputStream).build()
                );
            } catch (IOException | RuntimeException ex) {
                LOGGER.error(CONFIGURATION_LOG_MINDMAX, FAILURE.asANSI());
                LOGGER.error("Please make sure GeoLite2-City.mmdb is in classpath");
                throw new IllegalArgumentException(ex.getMessage());
            }
        } else {
            return new GeoMindMax(
                    this.jbstProperties.getUtilsConfigs().getGeoLocationsConfigs(),
                    null
            );
        }
    }

    // ================================================================================================================
    // INITIALIZERS: User Agent Details
    // ================================================================================================================
    private GeoUserAgentDetails initUserAgentDetails() {
        var enabled = this.jbstProperties.getUtilsConfigs().getUserAgentConfigs().isEnabled();
        LOGGER.info(CONFIGURATION_LOG_USER_AGENT_DETAILS, Status.of(enabled).asANSI());
        if (enabled) {
            try {
                LOGGER.info(CONFIGURATION_LOG_USER_AGENT_DETAILS, SUCCESS);
                return new GeoUserAgentDetails(
                        this.jbstProperties.getUtilsConfigs().getUserAgentConfigs(),
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
                    this.jbstProperties.getUtilsConfigs().getUserAgentConfigs(),
                    false,
                    null,
                    contactDevelopmentTeam("User agent configuration failure")
            );
        }
    }
}
