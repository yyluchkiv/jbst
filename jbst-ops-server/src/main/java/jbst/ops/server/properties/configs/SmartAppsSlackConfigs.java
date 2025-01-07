package jbst.ops.server.properties.configs;

import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryToggleProperty;
import jbst.foundation.domain.properties.base.AbstractTogglePropertyConfigs;
import jbst.ops.server.properties.atomics.SlackMainChannelCommunication;
import jbst.ops.server.properties.atomics.SlackTeamChannelCommunication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class SmartAppsSlackConfigs extends AbstractTogglePropertyConfigs implements SlackConfigs {
    @MandatoryProperty
    private final boolean enabled;
    @MandatoryToggleProperty
    private final String botToken;
    @MandatoryToggleProperty
    private final String appToken;
    @MandatoryToggleProperty
    private final SlackMainChannelCommunication communication;
    // TODO [YYL]: add teamsCommunicationsEnabled
//    @MandatoryToggleProperty
//    private final List<SlackTeamChannelCommunication> teamsCommunications;

    @Override
    public boolean isDisabled() {
        return !this.enabled;
    }
}
