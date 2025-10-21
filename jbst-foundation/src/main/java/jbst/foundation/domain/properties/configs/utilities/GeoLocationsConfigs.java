package jbst.foundation.domain.properties.configs.utilities;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;

@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class GeoLocationsConfigs extends JbstProperty {
    @MandatoryProperty
    private final boolean enabled;

    public static GeoLocationsConfigs hardcoded() {
        return new GeoLocationsConfigs(true);
    }

    public static GeoLocationsConfigs random() {
        return randomBoolean() ? enabled() : disabled();
    }

    public static GeoLocationsConfigs enabled() {
        return hardcoded();
    }

    public static GeoLocationsConfigs disabled() {
        return new GeoLocationsConfigs(false);
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean isToggle() {
        return this.enabled;
    }

    @Override
    public String getNameNonMandatory() {
        return JbstConstants.Symbols.DASH;
    }
}
