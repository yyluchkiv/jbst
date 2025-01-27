package jbst.ops.server.properties.base;

import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.NonMandatoryProperty;
import jbst.foundation.domain.properties.base.AbstractPropertyConfigs;
import jbst.ops.server.domain.servers.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.TRUE;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class SlackConfigs extends AbstractPropertyConfigs {
    @MandatoryProperty
    private final Team team;
    @MandatoryProperty
    private final Boolean main;
    @MandatoryProperty
    private final String botToken;
    @MandatoryProperty
    private final String appToken;
    @MandatoryProperty
    private final Mode mode;
    @MandatoryProperty
    private final String mainCommunicationId;
    @NonMandatoryProperty
    private final List<SlackTeamCommunication> teamsCommunications;

    public boolean isMain() {
        return TRUE.equals(this.main);
    }

    public boolean isReadOnlyMode() {
        return Mode.READONLY.equals(this.mode);
    }

    public Optional<SlackTeamCommunication> getTeamCommunication(@NotNull Team team) {
        return this.teamsCommunications.stream()
                .filter(tc -> tc.getTeam().equals(team))
                .findFirst();
    }

    public enum Mode {
        OPERATIONAL,
        READONLY
    }
}
