package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryPropertyToggle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;

@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyScheduledJob extends JbstProperty {
    @MandatoryProperty
    private final boolean enabled;
    @MandatoryPropertyToggle
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
