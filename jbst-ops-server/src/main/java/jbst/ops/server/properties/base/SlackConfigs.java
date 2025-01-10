package jbst.ops.server.properties.base;

import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.NonMandatoryProperty;
import jbst.foundation.domain.properties.base.AbstractPropertyConfigs;
import jbst.ops.server.domain.servers.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class SlackConfigs extends AbstractPropertyConfigs {
    @MandatoryProperty
    private final Team team;
    @MandatoryProperty
    private final String botToken;
    @MandatoryProperty
    private final String appToken;
    @MandatoryProperty
    private final Mode mode;
    @MandatoryProperty
    private final String mainCommunication;
    @NonMandatoryProperty
    private final List<SlackTeamCommunication> teamsCommunications;

    public boolean isReadOnlyMode() {
        return Mode.READONLY.equals(this.mode);
    }

    public enum Mode {
        OPERATIONAL,
        READONLY
    }
}
