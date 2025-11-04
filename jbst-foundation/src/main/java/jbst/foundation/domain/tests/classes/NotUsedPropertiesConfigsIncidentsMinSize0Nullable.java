package jbst.foundation.domain.tests.classes;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.properties.annotations.JbstPropertyMetadataMapMinSize;
import jbst.foundation.domain.properties.annotations.NonMandatoryProperty;
import jbst.foundation.domain.properties.base.JbstPropertyScheduledJob;
import jbst.foundation.domain.properties.base.JbstPropertySpringLogging;
import jbst.foundation.domain.properties.base.JbstPropertySpringServer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Map;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class NotUsedPropertiesConfigsIncidentsMinSize0Nullable extends JbstProperty {
    @JbstPropertyMandatory
    private final JbstPropertyScheduledJob scheduledJob;
    @JbstPropertyMandatory
    private final JbstPropertySpringServer springServer;
    @JbstPropertyMandatory
    private final JbstPropertySpringLogging springLogging;
    @NonMandatoryProperty
    @JbstPropertyMetadataMapMinSize(minSize = 0)
    private final Map<String, Boolean> types;

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
        return "not-used-properties-configs";
    }
}
