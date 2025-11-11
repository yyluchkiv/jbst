package jbst.server.ops.properties.base;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.server.ops.domain.servers.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyOpsSlackTeamCommunication extends JbstProperty {
    @JbstPropertyMandatory
    private final Team team;
    @JbstPropertyMandatory
    private final Mode mode;
    @JbstPropertyMandatory
    private final String id;

    public enum Mode {
        DISABLED,
        OPERATIONAL
    }

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

    public boolean isOperationalMode() {
        return Mode.OPERATIONAL.equals(this.mode);
    }
}
