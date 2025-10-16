package jbst.foundation.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.enums.Status;
import jbst.foundation.domain.geo.GeoCountryFlag;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.configs.utilities.GeoCountryFlagsConfigs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

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
@Component
public class JbstGeoUtils {
    private static final String CONFIGURATION_LOG = PREFIX + " Geo country flags geo-countries-flags.json — {}";

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

    // Properties
    private final JbstProperties jbstProperties;
    // State
    private final GeoFlags geoFlags;

    public JbstGeoUtils(
            ResourceLoader resourceLoader,
            JbstProperties jbstProperties
    ) {
        this.jbstProperties = jbstProperties;
        this.geoFlags = this.initFlags(resourceLoader);
    }

    // ================================================================================================================
    // FLAGS
    // ================================================================================================================
    public String getFlagEmojiByCountryName(String countryName) {
        return this.geoFlags.getEmojiByName(countryName);
    }

    public String getFlagEmojiByCountryCode(String countryCode) {
        return this.geoFlags.getEmojiByCode(countryCode);
    }

    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    private GeoFlags initFlags(ResourceLoader resourceLoader) {
        var geoCountryFlagsConfigs = this.jbstProperties.getUtilsConfigs().getGeoCountryFlagsConfigs();
        LOGGER.info(CONFIGURATION_LOG, Status.of(geoCountryFlagsConfigs.isEnabled()).asANSI());
        if (geoCountryFlagsConfigs.isEnabled()) {
            try {
                var resource = resourceLoader.getResource("classpath:geo-countries-flags.json");
                var typeReference = new TypeReference<List<GeoCountryFlag>>() {};
                var objectMapper = new ObjectMapper();
                var geoCountryFlags = objectMapper.readValue(resource.getInputStream(), typeReference);
                LOGGER.info(CONFIGURATION_LOG, SUCCESS.asANSI());
                return new GeoFlags(
                        this.jbstProperties.getUtilsConfigs().getGeoCountryFlagsConfigs(),
                        geoCountryFlags.stream().collect(toUnmodifiableMap(item -> item.name().toLowerCase(), identity())),
                        geoCountryFlags.stream().collect(toUnmodifiableMap(item -> item.code().toLowerCase(), identity()))
                );
            } catch (IOException | RuntimeException ex) {
                LOGGER.error(CONFIGURATION_LOG, FAILURE.asANSI());
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
}
