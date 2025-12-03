package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoUnit.HOURS;
import static jbst.foundation.domain.random.JbstRandom.randomChronoUnit;
import static jbst.foundation.domain.random.JbstRandom.randomIntegerGreaterThanZeroByBounds;

@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyTimeAmount extends JbstProperty {
    @JbstPropertyMandatory
    private final long amount;
    @JbstPropertyMandatory
    private final ChronoUnit unit;

    public static JbstPropertyTimeAmount hardcoded() {
        return new JbstPropertyTimeAmount(12L, HOURS);
    }

    public static JbstPropertyTimeAmount random() {
        return new JbstPropertyTimeAmount(randomIntegerGreaterThanZeroByBounds(1, 10), randomChronoUnit());
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
        return this.amount + " " + this.unit;
    }

    public jbst.foundation.domain.time.TimeAmount getTimeAmount() {
        return new jbst.foundation.domain.time.TimeAmount(
                this.amount,
                this.unit
        );
    }
}
