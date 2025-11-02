package jbst.foundation.domain.properties.configs.security;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertySecurityLogging extends JbstProperty {
    @MandatoryProperty
    private final Boolean advancedRequestLoggingEnabled;

    public static JbstPropertySecurityLogging random() {
        return randomBoolean() ? enabled() : disabled();
    }

    public static JbstPropertySecurityLogging hardcoded() {
        return JbstPropertySecurityLogging.enabled();
    }

    public static JbstPropertySecurityLogging enabled() {
        return new JbstPropertySecurityLogging(true);
    }

    public static JbstPropertySecurityLogging disabled() {
        return new JbstPropertySecurityLogging(false);
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.LEAF;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonLeaf() {
        return JbstConstants.Symbols.DASH;
    }

    public boolean isAdvancedRequestLoggingEnabled() {
        return this.advancedRequestLoggingEnabled;
    }
}
