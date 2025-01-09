package jbst.ops.server.properties.configs;

import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.configs.AbstractPropertiesConfigs;
import jbst.ops.server.properties.base.SlackConfigs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;

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
}
