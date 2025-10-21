package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
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
public class EventsConfigs extends AbstractPropertiesConfigs {
    @MandatoryProperty
    private final String threadNamePrefix;
    @MandatoryProperty
    private final BigDecimal threadsCorePoolPercentage;
    @MandatoryProperty
    private final BigDecimal threadsMaxPoolPercentage;

    public static EventsConfigs hardcoded() {
        return new EventsConfigs("jbst-events", new BigDecimal("75"), HUNDRED);
    }

    public static EventsConfigs random() {
        return new EventsConfigs(randomString(), new BigDecimal("25"), HUNDRED);
    }

    @Override
    public boolean isParentPropertiesNode() {
        return true;
    }

    @Override
    public PropertyId getPropertyId() {
        return new PropertyId("events-configs");
    }

    public TuplePercentage asThreadsCorePoolTuplePercentage() {
        return TuplePercentage.progressTuplePercentage(this.threadsCorePoolPercentage, HUNDRED);
    }

    public TuplePercentage asThreadsMaxPoolTuplePercentage() {
        return TuplePercentage.progressTuplePercentage(this.threadsMaxPoolPercentage, HUNDRED);
    }
}
