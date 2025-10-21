package jbst.server.ops.properties.base;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.AbstractProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.server.ops.domain.servers.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class SlackTeamCommunication extends AbstractProperty {
    @MandatoryProperty
    private final Team team;
    @MandatoryProperty
    private final Mode mode;
    @MandatoryProperty
    private final String id;

    public enum Mode {
        DISABLED,
        OPERATIONAL
    }

    @Override
    public boolean isParent() {
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

    public boolean isOperationalMode() {
        return Mode.OPERATIONAL.equals(this.mode);
    }
}
