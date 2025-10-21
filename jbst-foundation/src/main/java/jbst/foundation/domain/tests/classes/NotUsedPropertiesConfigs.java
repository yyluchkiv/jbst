package jbst.foundation.domain.tests.classes;

import jbst.foundation.domain.properties.AbstractJbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.ScheduledJob;
import jbst.foundation.domain.properties.base.SpringLogging;
import jbst.foundation.domain.properties.base.SpringServer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class NotUsedPropertiesConfigs extends AbstractJbstProperty {
    @MandatoryProperty
    private final ScheduledJob scheduledJob;
    @MandatoryProperty
    private final SpringServer springServer;
    @MandatoryProperty
    private final SpringLogging springLogging;

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonMandatory() {
        return "not-used-properties-configs";
    }
}
