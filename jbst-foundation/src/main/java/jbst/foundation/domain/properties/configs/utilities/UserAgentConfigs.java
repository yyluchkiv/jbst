package jbst.foundation.domain.properties.configs.utilities;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.AbstractJbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;

@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class UserAgentConfigs extends AbstractJbstProperty {
    @MandatoryProperty
    private final boolean enabled;

    public static UserAgentConfigs hardcoded() {
        return new UserAgentConfigs(true);
    }

    public static UserAgentConfigs random() {
        return randomBoolean() ? enabled() : disabled();
    }

    public static UserAgentConfigs enabled() {
        return hardcoded();
    }

    public static UserAgentConfigs disabled() {
        return new UserAgentConfigs(false);
    }

    @Override
    public boolean isParent() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean isToggle() {
        return true;
    }

    @Override
    public String getNameNonMandatory() {
        return JbstConstants.Symbols.DASH;
    }
}
