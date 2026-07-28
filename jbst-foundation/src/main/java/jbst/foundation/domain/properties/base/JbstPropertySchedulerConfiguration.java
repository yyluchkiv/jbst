package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.time.JbstSchedulerConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static jbst.foundation.domain.random.JbstRandom.randomLongGreaterThanZeroByBounds;
import static jbst.foundation.domain.random.JbstRandom.randomTimeUnit;

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

    public static JbstPropertySchedulerConfiguration fixed() {
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
    public JbstSchedulerConfiguration getSchedulerConfiguration() {
        return new JbstSchedulerConfiguration(
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
