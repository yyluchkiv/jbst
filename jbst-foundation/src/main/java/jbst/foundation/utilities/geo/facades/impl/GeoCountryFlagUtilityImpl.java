package jbst.foundation.utilities.geo.facades.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.enums.Status;
import jbst.foundation.domain.geo.GeoCountryFlag;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.utilities.geo.facades.GeoCountryFlagUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static jbst.foundation.domain.constants.JbstConstants.Logs.PREFIX;
import static jbst.foundation.domain.enums.Status.FAILURE;
import static jbst.foundation.domain.enums.Status.SUCCESS;

@Deprecated(forRemoval = true)
@Slf4j
public class GeoCountryFlagUtilityImpl implements GeoCountryFlagUtility {
    private static final String CONFIGURATION_LOG = PREFIX + " Geo country flags geo-countries-flags.json — {}";

    private final Map<String, GeoCountryFlag> mappedByCountryName;
    private final Map<String, GeoCountryFlag> mappedByCountryCode;

    // Properties
    private final JbstProperties jbstProperties;

    public GeoCountryFlagUtilityImpl(
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
                this.mappedByCountryName = geoCountryFlags.stream()
                        .collect(
                                Collectors.toMap(
                                        item -> item.name().toLowerCase(),
                                        Function.identity()
                                )
                        );
                this.mappedByCountryCode = geoCountryFlags.stream()
                        .collect(
                                Collectors.toMap(
                                        item -> item.code().toLowerCase(),
                                        Function.identity()
                                )
                        );
                LOGGER.info(CONFIGURATION_LOG, SUCCESS.formatAnsi());
            } catch (IOException | RuntimeException ex) {
                LOGGER.error(CONFIGURATION_LOG, FAILURE.formatAnsi());
                LOGGER.error("Please make sure geo-countries-flags.json is in classpath");
                throw new IllegalArgumentException(ex.getMessage());
            }
        } else {
            this.mappedByCountryName = new HashMap<>();
            this.mappedByCountryCode = new HashMap<>();
        }
    }

    @Override
    public String getFlagEmojiByCountry(String country) {
        return this.getEmoji(
                this.mappedByCountryName,
                country
        );
    }

    @Override
    public String getFlagEmojiByCountryCode(String countryCode) {
        return this.getEmoji(
                this.mappedByCountryCode,
                countryCode
        );
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
