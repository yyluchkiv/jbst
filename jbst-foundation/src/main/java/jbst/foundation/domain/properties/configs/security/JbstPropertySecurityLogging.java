package jbst.foundation.domain.properties.configs.security;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.domain.random.JbstRandom.randomBoolean;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertySecurityLogging extends JbstProperty {
    @JbstPropertyMandatory
    private final Boolean advancedRequestLoggingEnabled;

    public static JbstPropertySecurityLogging random() {
        return randomBoolean() ? enabled() : disabled();
    }

    public static JbstPropertySecurityLogging fixed() {
        return JbstPropertySecurityLogging.disabled();
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
