package jbst.foundation.domain.properties.configs.security.jwt;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.AbstractJbstProperty;
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
public class LoggingConfigs extends AbstractJbstProperty {
    @MandatoryProperty
    private final Boolean advancedRequestLoggingEnabled;

    public static LoggingConfigs random() {
        return randomBoolean() ? enabled() : disabled();
    }

    public static LoggingConfigs hardcoded() {
        return LoggingConfigs.enabled();
    }

    public static LoggingConfigs enabled() {
        return new LoggingConfigs(true);
    }

    public static LoggingConfigs disabled() {
        return new LoggingConfigs(false);
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
        return false;
    }

    @Override
    public String getNameNonMandatory() {
        return JbstConstants.Symbols.DASH;
    }

    public boolean isAdvancedRequestLoggingEnabled() {
        return this.advancedRequestLoggingEnabled;
    }
}
