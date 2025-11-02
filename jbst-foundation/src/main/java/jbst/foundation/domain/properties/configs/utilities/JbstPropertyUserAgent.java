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
public class JbstPropertyUserAgent extends JbstProperty {
    @MandatoryProperty
    private final boolean enabled;

    public static JbstPropertyUserAgent hardcoded() {
        return new JbstPropertyUserAgent(true);
    }

    public static JbstPropertyUserAgent random() {
        return randomBoolean() ? enabled() : disabled();
    }

    public static JbstPropertyUserAgent enabled() {
        return hardcoded();
    }

    public static JbstPropertyUserAgent disabled() {
        return new JbstPropertyUserAgent(false);
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
