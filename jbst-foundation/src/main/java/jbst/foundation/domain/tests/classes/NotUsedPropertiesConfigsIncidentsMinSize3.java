package jbst.foundation.domain.tests.classes;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.properties.annotations.JbstPropertyMetadataMapMinSize;
import jbst.foundation.domain.properties.base.JbstPropertyScheduledJob;
import jbst.foundation.domain.properties.base.JbstPropertySpringLogging;
import jbst.foundation.domain.properties.base.JbstPropertySpringServer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Map;
import java.util.Set;

import static jbst.foundation.domain.asserts.Asserts.assertTrueOrThrow;
import static jbst.foundation.domain.constants.JbstConstants.JColor.RED_TEXT;
import static jbst.foundation.utilities.collections.JbstCollections.baseJoiningRaw;
import static org.apache.commons.collections4.SetUtils.disjunction;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class NotUsedPropertiesConfigsIncidentsMinSize3 extends JbstProperty {
    @JbstPropertyMandatory
    private final JbstPropertyScheduledJob scheduledJob;
    @JbstPropertyMandatory
    private final JbstPropertySpringServer springServer;
    @JbstPropertyMandatory
    private final JbstPropertySpringLogging springLogging;
    @JbstPropertyMandatory
    @JbstPropertyMetadataMapMinSize(minSize = 3)
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

    public void assertPropertiesExtended(Set<String> keys) {
        assertTrueOrThrow(
                this.types.size() >= keys.size(),
                "Property %s is invalid. Options: [%s]. Required: [%s]. Disjunction: [%s]".formatted(
                        "not-used-properties-configs.types",
                        baseJoiningRaw(this.types.entrySet()),
                        baseJoiningRaw(keys),
                        RED_TEXT.format(baseJoiningRaw(disjunction(this.types.keySet(), keys)))
                )
        );
    }
}
