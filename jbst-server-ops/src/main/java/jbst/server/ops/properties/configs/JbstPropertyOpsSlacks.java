package jbst.server.ops.properties.configs;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.server.ops.domain.servers.Team;
import jbst.server.ops.properties.base.JbstPropertyOpsSlack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;

import static jbst.foundation.domain.asserts.Asserts.assertTrueOrThrow;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyOpsSlacks extends JbstProperty {
    @JbstPropertyMandatory
    private final List<JbstPropertyOpsSlack> values;

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
        return "slack";
    }

    @Override
    public void assertProperties() {
        super.assertProperties();
        assertTrueOrThrow(
                this.values.stream().map(JbstPropertyOpsSlack::isMain).filter(Boolean::booleanValue).count() == 1,
                "Slacks configs must have one main team"
        );
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public JbstPropertyOpsSlack getMainSlackConfig() {
        return this.values.stream()
                .filter(JbstPropertyOpsSlack::isMain)
                .findFirst()
                .get();
    }

    public Team getMainTeam() {
        return this.getMainSlackConfig().getTeam();
    }
}
