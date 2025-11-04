package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.tuples.TuplePercentage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.math.BigDecimal;

import static jbst.foundation.domain.constants.JbstConstants.Numbers.BigDecimals.HUNDRED;
import static jbst.foundation.utilities.random.RandomUtility.randomString;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyAsync extends JbstProperty {
    @JbstPropertyMandatory
    private final String threadNamePrefix;
    @JbstPropertyMandatory
    private final BigDecimal threadsCorePoolPercentage;
    @JbstPropertyMandatory
    private final BigDecimal threadsMaxPoolPercentage;

    public static JbstPropertyAsync hardcoded() {
        return new JbstPropertyAsync("jbst-async", new BigDecimal("25"), HUNDRED);
    }

    public static JbstPropertyAsync random() {
        return new JbstPropertyAsync(randomString(), new BigDecimal("25"), HUNDRED);
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.ROOT;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonLeaf() {
        return "async-configs";
    }

    public TuplePercentage asThreadsCorePoolTuplePercentage() {
        return TuplePercentage.progressTuplePercentage(this.threadsCorePoolPercentage, HUNDRED);
    }

    public TuplePercentage asThreadsMaxPoolTuplePercentage() {
        return TuplePercentage.progressTuplePercentage(this.threadsMaxPoolPercentage, HUNDRED);
    }
}
