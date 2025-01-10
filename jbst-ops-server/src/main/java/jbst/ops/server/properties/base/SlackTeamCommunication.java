package jbst.ops.server.properties.base;

import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.AbstractPropertyConfigs;
import jbst.ops.server.domain.servers.TeamV2;
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
    private final TeamV2 team;
    @MandatoryProperty
    private final Mode mode;
    @MandatoryProperty
    private final String name;

    public enum Mode {
        DISABLED,
        OPERATIONAL
    }
}
