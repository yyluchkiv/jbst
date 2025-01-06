package jbst.ops.server.properties.configs;

import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.AbstractTogglePropertyConfigs;
import jbst.ops.server.properties.atomics.SlackMainChannelCommunication;
import jbst.ops.server.properties.atomics.SlackTeamChannelCommunication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;
import java.util.Optional;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class Tech1SlackConfigs extends AbstractTogglePropertyConfigs implements SlackConfigs {
    @MandatoryProperty
    private final boolean enabled;
    @MandatoryProperty
    private final String botToken;
    @MandatoryProperty
    private final String appToken;
    @MandatoryProperty
    private final SlackMainChannelCommunication communication;
    @MandatoryProperty
    private final List<String> founders;
    @MandatoryProperty
    private final List<SlackTeamChannelCommunication> teamsCommunications;

    @Override
    public boolean isDisabled() {
        return !this.enabled;
    }

    public Optional<SlackTeamChannelCommunication> getTeamBy(String userChannel) {
        return this.teamsCommunications.stream()
                .filter(teamCommunication -> teamCommunication.getCommunication().isEnabled())
                .filter(teamCommunication -> userChannel.equals(teamCommunication.getCommunication().getChannel()))
                .findFirst();
    }
}
