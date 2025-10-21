package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.properties.AbstractJbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.configs.utilities.GeoCountryFlagsConfigs;
import jbst.foundation.domain.properties.configs.utilities.GeoLocationsConfigs;
import jbst.foundation.domain.properties.configs.utilities.UserAgentConfigs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class UtilsConfigs extends AbstractJbstProperty {
    @MandatoryProperty
    private final GeoLocationsConfigs geoLocationsConfigs;
    @MandatoryProperty
    private final GeoCountryFlagsConfigs geoCountryFlagsConfigs;
    @MandatoryProperty
    private final UserAgentConfigs userAgentConfigs;

    public static UtilsConfigs hardcoded() {
        return new UtilsConfigs(
                GeoLocationsConfigs.disabled(),
                GeoCountryFlagsConfigs.enabled(),
                UserAgentConfigs.enabled()
        );
    }

    public static UtilsConfigs random() {
        return new UtilsConfigs(
                GeoLocationsConfigs.random(),
                GeoCountryFlagsConfigs.random(),
                UserAgentConfigs.random()
        );
    }

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonMandatory() {
        return "utils-configs";
    }
}
