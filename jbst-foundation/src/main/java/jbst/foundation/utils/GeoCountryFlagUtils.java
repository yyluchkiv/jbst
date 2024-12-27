package jbst.foundation.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.enums.Status;
import jbst.foundation.domain.geo.GeoCountryFlag;
import jbst.foundation.domain.properties.JbstProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
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

@Slf4j
public class GeoCountryFlagUtils {
    private static final String CONFIGURATION_LOG = PREFIX + " Geo country flags geo-countries-flags.json — {}";

    private final Map<String, GeoCountryFlag> names;
    private final Map<String, GeoCountryFlag> codes;

    // Properties
    private final JbstProperties jbstProperties;

    public GeoCountryFlagUtils(
            ResourceLoader resourceLoader,
            JbstProperties jbstProperties
    ) {
        this.jbstProperties = jbstProperties;
        var geoCountryFlagsConfigs = this.jbstProperties.getUtilitiesConfigs().getGeoCountryFlagsConfigs();
        LOGGER.info(CONFIGURATION_LOG, Status.of(geoCountryFlagsConfigs.isEnabled()).formatAnsi());
        if (geoCountryFlagsConfigs.isEnabled()) {
            try {
                var resource = resourceLoader.getResource("classpath:geo-countries-flags.json");
                var typeReference = new TypeReference<List<GeoCountryFlag>>() {};
                var objectMapper = new ObjectMapper();
                var geoCountryFlags = objectMapper.readValue(resource.getInputStream(), typeReference);
                this.names = geoCountryFlags.stream().collect(toUnmodifiableMap(item -> item.name().toLowerCase(), identity()));
                this.codes = geoCountryFlags.stream().collect(toUnmodifiableMap(item -> item.code().toLowerCase(), identity()));
                LOGGER.info(CONFIGURATION_LOG, SUCCESS.formatAnsi());
            } catch (IOException | RuntimeException ex) {
                LOGGER.error(CONFIGURATION_LOG, FAILURE.formatAnsi());
                LOGGER.error("Please make sure geo-countries-flags.json is in classpath");
                throw new IllegalArgumentException(ex.getMessage());
            }
        } else {
            this.names = unmodifiableMap(new HashMap<>());
            this.codes = unmodifiableMap(new HashMap<>());
        }
    }

    public final String getFlagEmojiByCountry(String country) {
        return this.getEmoji(this.names, country);
    }

    public final String getFlagEmojiByCountryCode(String countryCode) {
        return this.getEmoji(this.codes, countryCode);
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private String getEmoji(Map<String, GeoCountryFlag> mappedBy, String searchKey) {
        if (!this.jbstProperties.getUtilitiesConfigs().getGeoCountryFlagsConfigs().isEnabled()) {
            return GeoCountryFlag.unknown().emoji();
        }
        if (isNull(searchKey)) {
            searchKey = JbstConstants.Strings.UNKNOWN.toLowerCase();
        }
        return mappedBy.getOrDefault(searchKey.toLowerCase(), GeoCountryFlag.unknown()).emoji();
    }
}
