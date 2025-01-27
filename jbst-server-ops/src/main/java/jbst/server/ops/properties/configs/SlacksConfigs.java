package jbst.server.ops.properties.configs;

import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.configs.AbstractPropertiesConfigs;
import jbst.server.ops.domain.servers.Team;
import jbst.server.ops.properties.base.SlackConfigs;
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
public class SlacksConfigs extends AbstractPropertiesConfigs {
    @MandatoryProperty
    private final List<SlackConfigs> values;

    @Override
    public boolean isParentPropertiesNode() {
        return true;
    }

    @Override
    public void assertProperties(PropertyId propertyId) {
        super.assertProperties(propertyId);
        assertTrueOrThrow(
                this.values.stream().map(SlackConfigs::isMain).filter(Boolean::booleanValue).count() == 1,
                "Slacks configs must have one main team"
        );
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public SlackConfigs getMainSlackConfig() {
        return this.values.stream()
                .filter(SlackConfigs::isMain)
                .findFirst()
                .get();
    }

    public Team getMainTeam() {
        return this.getMainSlackConfig().getTeam();
    }
}
