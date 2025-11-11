package jbst.foundation.domain.properties.configs.utilities;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;

@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyCountriesFlags extends JbstProperty {
    @JbstPropertyMandatory
    private final boolean enabled;

    public static JbstPropertyCountriesFlags hardcoded() {
        return new JbstPropertyCountriesFlags(true);
    }

    public static JbstPropertyCountriesFlags random() {
        return randomBoolean() ? enabled() : disabled();
    }

    public static JbstPropertyCountriesFlags enabled() {
        return hardcoded();
    }

    public static JbstPropertyCountriesFlags disabled() {
        return new JbstPropertyCountriesFlags(false);
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.LEAF;
    }

    @Override
    public boolean isToggle() {
        return this.enabled;
    }

    @Override
    public String getNameNonLeaf() {
        return JbstConstants.Symbols.DASH;
    }
}
