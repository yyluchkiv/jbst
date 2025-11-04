package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static jbst.foundation.utilities.random.RandomUtility.randomLongGreaterThanZeroByBounds;
import static jbst.foundation.utilities.random.RandomUtility.randomTimeUnit;

@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertySchedulerConfiguration extends JbstProperty {
    @JbstPropertyMandatory
    private final Long initialDelay;
    @JbstPropertyMandatory
    private final Long delay;
    @JbstPropertyMandatory
    private final TimeUnit unit;

    public static JbstPropertySchedulerConfiguration hardcoded() {
        return new JbstPropertySchedulerConfiguration(30L, 30L, SECONDS);
    }

    public static JbstPropertySchedulerConfiguration random() {
        return new JbstPropertySchedulerConfiguration(
                randomLongGreaterThanZeroByBounds(15, 45),
                randomLongGreaterThanZeroByBounds(15, 45),
                randomTimeUnit()
        );
    }

    @SuppressWarnings("unused")
    public jbst.foundation.domain.time.SchedulerConfiguration getSchedulerConfiguration() {
        return new jbst.foundation.domain.time.SchedulerConfiguration(
                this.initialDelay,
                this.delay,
                this.unit
        );
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

    @Override
    public String toString() {
        return "[" + this.initialDelay + ", " + this.delay + ", " + this.unit + "]";
    }
}
