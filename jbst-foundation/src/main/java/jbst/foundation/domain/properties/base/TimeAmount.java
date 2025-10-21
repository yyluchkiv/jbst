package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.AbstractJbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoUnit.HOURS;
import static jbst.foundation.utilities.random.RandomUtility.randomChronoUnit;
import static jbst.foundation.utilities.random.RandomUtility.randomIntegerGreaterThanZeroByBounds;

@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class TimeAmount extends AbstractJbstProperty {
    @MandatoryProperty
    private final long amount;
    @MandatoryProperty
    private final ChronoUnit unit;

    public static TimeAmount hardcoded() {
        return new TimeAmount(12L, HOURS);
    }

    public static TimeAmount random() {
        return new TimeAmount(randomIntegerGreaterThanZeroByBounds(1, 10), randomChronoUnit());
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

    @Override
    public String toString() {
        return this.amount + " " + this.unit;
    }

    public jbst.foundation.domain.time.TimeAmount getTimeAmount() {
        return new jbst.foundation.domain.time.TimeAmount(
                this.amount,
                this.unit
        );
    }
}
