package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.properties.configs.utilities.JbstPropertyCountriesFlags;
import jbst.foundation.domain.properties.configs.utilities.JbstPropertyGeolocations;
import jbst.foundation.domain.properties.configs.utilities.JbstPropertyUsersAgents;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyUtils extends JbstProperty {
    @JbstPropertyMandatory
    private final JbstPropertyGeolocations geolocations;
    @JbstPropertyMandatory
    private final JbstPropertyCountriesFlags countriesFlags;
    @JbstPropertyMandatory
    private final JbstPropertyUsersAgents usersAgents;

    public static JbstPropertyUtils hardcoded() {
        return new JbstPropertyUtils(
                JbstPropertyGeolocations.disabled(),
                JbstPropertyCountriesFlags.enabled(),
                JbstPropertyUsersAgents.enabled()
        );
    }

    public static JbstPropertyUtils random() {
        return new JbstPropertyUtils(
                JbstPropertyGeolocations.random(),
                JbstPropertyCountriesFlags.random(),
                JbstPropertyUsersAgents.random()
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
        return "utils";
    }
}
