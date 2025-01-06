package jbst.ops.server.properties.configs;

import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.AbstractTogglePropertyConfigs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import jbst.ops.server.properties.atomics.SlackMainChannelCommunication;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class SmartAppsSlackConfigs extends AbstractTogglePropertyConfigs implements SlackConfigs {
    @MandatoryProperty
    private final boolean enabled;
    @MandatoryProperty
    private final String botToken;
    @MandatoryProperty
    private final String appToken;
    @MandatoryProperty
    private final SlackMainChannelCommunication communication;

    @Override
    public boolean isDisabled() {
        return !this.enabled;
    }
}
