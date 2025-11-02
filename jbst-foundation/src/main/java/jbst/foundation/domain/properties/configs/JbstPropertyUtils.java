package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.configs.utilities.JbstPropertyGeoCountryFlags;
import jbst.foundation.domain.properties.configs.utilities.JbstPropertyGeoLocations;
import jbst.foundation.domain.properties.configs.utilities.JbstPropertyUserAgent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyUtils extends JbstProperty {
    @MandatoryProperty
    private final JbstPropertyGeoLocations geoLocationsConfigs;
    @MandatoryProperty
    private final JbstPropertyGeoCountryFlags geoCountryFlagsConfigs;
    @MandatoryProperty
    private final JbstPropertyUserAgent userAgentConfigs;

    public static JbstPropertyUtils hardcoded() {
        return new JbstPropertyUtils(
                JbstPropertyGeoLocations.disabled(),
                JbstPropertyGeoCountryFlags.enabled(),
                JbstPropertyUserAgent.enabled()
        );
    }

    public static JbstPropertyUtils random() {
        return new JbstPropertyUtils(
                JbstPropertyGeoLocations.random(),
                JbstPropertyGeoCountryFlags.random(),
                JbstPropertyUserAgent.random()
        );
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.ROOT;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonLeaf() {
        return "utils-configs";
    }
}
