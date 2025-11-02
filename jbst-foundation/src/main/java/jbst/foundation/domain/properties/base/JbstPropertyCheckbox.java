package jbst.foundation.domain.properties.base;

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
public class JbstPropertyCheckbox extends JbstProperty {
    @MandatoryProperty
    private final boolean enabled;

    public static JbstPropertyCheckbox hardcoded() {
        return new JbstPropertyCheckbox(true);
    }

    public static JbstPropertyCheckbox random() {
        return randomBoolean() ? enabled() : disabled();
    }

    public static JbstPropertyCheckbox enabled() {
        return hardcoded();
    }

    public static JbstPropertyCheckbox disabled() {
        return new JbstPropertyCheckbox(false);
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
