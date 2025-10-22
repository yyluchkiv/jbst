package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryToggleProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;

@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class ScheduledJob extends JbstProperty {
    @MandatoryProperty
    private final boolean enabled;
    @MandatoryToggleProperty
    private SchedulerConfiguration configuration;

    public static ScheduledJob hardcoded() {
        return new ScheduledJob(true, SchedulerConfiguration.hardcoded());
    }

    public static ScheduledJob random() {
        return randomBoolean() ? enabled() : disabled();
    }

    public static ScheduledJob enabled() {
        return hardcoded();
    }


    public static ScheduledJob disabled() {
        return new ScheduledJob(false, null);
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
