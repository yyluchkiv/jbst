package jbst.server.ops.properties.base;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.properties.annotations.JbstPropertyOptional;
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
public class JbstPropertyOpsSlack extends JbstProperty {
    @JbstPropertyMandatory
    private final Team team;
    @JbstPropertyMandatory
    private final Boolean main;
    @JbstPropertyMandatory
    private final String botToken;
    @JbstPropertyMandatory
    private final String appToken;
    @JbstPropertyMandatory
    private final Mode mode;
    @JbstPropertyMandatory
    private final String mainCommunicationId;
    @JbstPropertyOptional
    private final List<JbstPropertyOpsSlackTeamCommunication> teamsCommunications;

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.LEAF;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonLeaf() {
        return JbstConstants.Symbols.DASH;
    }

    public boolean isMain() {
        return TRUE.equals(this.main);
    }

    public boolean isReadOnlyMode() {
        return Mode.READONLY.equals(this.mode);
    }

    public Optional<JbstPropertyOpsSlackTeamCommunication> getTeamCommunication(@NotNull Team team) {
        return this.teamsCommunications.stream()
                .filter(tc -> tc.getTeam().equals(team))
                .findFirst();
    }

    public enum Mode {
        OPERATIONAL,
        READONLY
    }
}
