package jbst.foundation.domain.tests.classes;

import jbst.foundation.domain.annotations.JbstModificationBeta;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryPropertyMapMinSize;
import jbst.foundation.domain.properties.base.ScheduledJob;
import jbst.foundation.domain.properties.base.SpringLogging;
import jbst.foundation.domain.properties.base.SpringServer;
import jbst.foundation.utilities.collections.CollectionUtility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Map;
import java.util.Set;

import static jbst.foundation.domain.asserts.Asserts.assertTrueOrThrow;
import static jbst.foundation.domain.constants.JbstConstants.JColor.RED_TEXT;
import static jbst.foundation.utilities.collections.CollectionUtility.baseJoiningRaw;
import static org.apache.commons.collections4.SetUtils.disjunction;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class NotUsedPropertiesConfigs extends JbstProperty {
    @MandatoryProperty
    private final ScheduledJob scheduledJob;
    @MandatoryProperty
    private final SpringServer springServer;
    @MandatoryProperty
    private final SpringLogging springLogging;
    @MandatoryProperty
    @MandatoryPropertyMapMinSize(minSize = 3)
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

    @JbstModificationBeta(releaseVersion = "v1.31")
    public void assertPropertiesExtended(Set<String> keys) {
        assertTrueOrThrow(
                this.types.size() >= keys.size(),
                "Property %s is invalid. Options: [%s]. Required: [%s]. Disjunction: [%s]".formatted(
                        "not-used-properties-configs.types",
                        baseJoiningRaw(this.types.entrySet()),
                        baseJoiningRaw(keys),
                        RED_TEXT.format(CollectionUtility.baseJoiningRaw(disjunction(this.types.keySet(), keys)))
                )
        );
    }
}
