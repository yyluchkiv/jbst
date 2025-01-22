package jbst.ops.server.properties.base;

import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.AbstractPropertyConfigs;
import jbst.ops.server.domain.servers.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class SlackTeamCommunication extends AbstractPropertyConfigs {
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

    public boolean isOperationalMode() {
        return Mode.OPERATIONAL.equals(this.mode);
    }
}
