package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.properties.annotations.JbstPropertyOptional;
import jbst.foundation.domain.tuples.TuplePercentage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.math.BigDecimal;

import static jbst.foundation.domain.constants.JbstConstants.Numbers.BigDecimals.HUNDRED;
import static jbst.foundation.domain.properties.JbstPropertiesAsserts.assertNonNullOrThrow;
import static jbst.foundation.domain.random.JbstRandom.randomString;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyAsync extends JbstProperty {
    /**
     * Opt-in, Java 21 virtual threads (JEP 444). false — platform-thread pool
     */
    @JbstPropertyMandatory
    private final Boolean virtualThreads;
    @JbstPropertyMandatory
    private final String threadNamePrefix;
    /**
     * Platform-thread pool sizing. Required only when virtual-threads: false
     */
    @JbstPropertyOptional
    private final BigDecimal threadsCorePoolPercentage;
    @JbstPropertyOptional
    private final BigDecimal threadsMaxPoolPercentage;

    public static JbstPropertyAsync fixed() {
        return new JbstPropertyAsync(true, "jbst-async", new BigDecimal("25"), HUNDRED);
    }

    public static JbstPropertyAsync random() {
        return new JbstPropertyAsync(true, randomString(), new BigDecimal("25"), HUNDRED);
    }

    public boolean isVirtualThreadsEnabled() {
        return Boolean.TRUE.equals(this.virtualThreads);
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
        return "async";
    }

    public TuplePercentage asThreadsCorePoolTuplePercentage() {
        assertNonNullOrThrow(this.threadsCorePoolPercentage, "async.threads-core-pool-percentage");
        return TuplePercentage.progressTuplePercentage(this.threadsCorePoolPercentage, HUNDRED);
    }

    public TuplePercentage asThreadsMaxPoolTuplePercentage() {
        assertNonNullOrThrow(this.threadsMaxPoolPercentage, "async.threads-max-pool-percentage");
        return TuplePercentage.progressTuplePercentage(this.threadsMaxPoolPercentage, HUNDRED);
    }
}
