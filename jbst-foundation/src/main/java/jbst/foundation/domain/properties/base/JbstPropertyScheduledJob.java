package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatoryOnToggleEnabled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;

@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyScheduledJob extends JbstProperty {
    @JbstPropertyMandatory
    private final boolean enabled;
    @JbstPropertyMandatoryOnToggleEnabled
    private JbstPropertySchedulerConfiguration configuration;

    public static JbstPropertyScheduledJob hardcoded() {
        return new JbstPropertyScheduledJob(true, JbstPropertySchedulerConfiguration.hardcoded());
    }

    public static JbstPropertyScheduledJob random() {
        return randomBoolean() ? enabled() : disabled();
    }

    public static JbstPropertyScheduledJob enabled() {
        return hardcoded();
    }


    public static JbstPropertyScheduledJob disabled() {
        return new JbstPropertyScheduledJob(false, null);
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
