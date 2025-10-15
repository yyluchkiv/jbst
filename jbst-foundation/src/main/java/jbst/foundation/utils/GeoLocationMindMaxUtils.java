package jbst.foundation.utils;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import jbst.foundation.domain.enums.Status;
import jbst.foundation.domain.exceptions.geo.JbstGeoLocationNotFoundException;
import jbst.foundation.domain.geo.GeoLocation;
import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.domain.properties.JbstProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.net.InetAddress;

import static jbst.foundation.domain.constants.JbstConstants.Logs.PREFIX;
import static jbst.foundation.domain.enums.Status.FAILURE;
import static jbst.foundation.domain.enums.Status.SUCCESS;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.contactDevelopmentTeam;

@Slf4j
public final class GeoLocationMindMaxUtils {
    private static final String CONFIGURATION_LOG = PREFIX + " Geo location database GeoLite2-City.mmdb — {}";

    // Database
    private final DatabaseReader databaseReader;
    // Utilities
    private final GeoCountryFlagUtils geoCountryFlagUtils;
    // Properties
    private final JbstProperties jbstProperties;

    public GeoLocationMindMaxUtils(
            ResourceLoader resourceLoader,
            GeoCountryFlagUtils geoCountryFlagUtils,
            JbstProperties jbstProperties
    ) {
        this.geoCountryFlagUtils = geoCountryFlagUtils;
        this.jbstProperties = jbstProperties;
        var enabled = jbstProperties.getUtilsConfigs().getGeoLocationsConfigs().isEnabled();
        LOGGER.info(CONFIGURATION_LOG, Status.of(enabled).asANSI());
        if (enabled) {
            try {
                var resource = resourceLoader.getResource("classpath:GeoLite2-City.mmdb");
                var inputStream = resource.getInputStream();
                this.databaseReader = new DatabaseReader.Builder(inputStream).build();
                LOGGER.info(CONFIGURATION_LOG, SUCCESS.asANSI());
            } catch (IOException | RuntimeException ex) {
                LOGGER.error(CONFIGURATION_LOG, FAILURE.asANSI());
                LOGGER.error("Please make sure GeoLite2-City.mmdb is in classpath");
                throw new IllegalArgumentException(ex.getMessage());
            }
        } else {
            this.databaseReader = null;
        }
    }

    public GeoLocation getGeoLocation(IPAddress ipAddress) throws JbstGeoLocationNotFoundException {
        if (!this.jbstProperties.getUtilsConfigs().getGeoLocationsConfigs().isEnabled()) {
            return GeoLocation.unknown(ipAddress, contactDevelopmentTeam("Geo configurations failure"));
        }
        try {
            var inetAddress = InetAddress.getByName(ipAddress.value());
            var response = this.databaseReader.city(inetAddress);
            var countryCode = response.getCountry().getIsoCode();
            var countryFlag = this.geoCountryFlagUtils.getFlagEmojiByCountryCode(countryCode);
            return GeoLocation.processed(
                    ipAddress,
                    response.getCountry().getName(),
                    countryCode,
                    countryFlag,
                    response.getCity().getName()
            );
        } catch (IOException | GeoIp2Exception ex) {
            throw new JbstGeoLocationNotFoundException(ex.getMessage());
        }
    }
}
