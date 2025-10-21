package jbst.server.ops.properties.base;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.NonMandatoryProperty;
import jbst.server.ops.domain.servers.Team;
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
public class SlackConfigs extends JbstProperty {
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

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonMandatory() {
        return JbstConstants.Symbols.DASH;
    }

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
