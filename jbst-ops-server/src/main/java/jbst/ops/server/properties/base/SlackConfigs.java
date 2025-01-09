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
public class SlackConfigs extends AbstractPropertyConfigs {
    @MandatoryProperty
    private final TeamV2 team;
    @MandatoryProperty
    private final String botToken;
    @MandatoryProperty
    private final String appToken;
    @MandatoryProperty
    private final Mode communicationMode;

    public boolean isCommunicationReadOnly() {
        return Mode.READONLY.equals(this.communicationMode);
    }

    public enum Mode {
        OPERATIONAL,
        READONLY
    }
}
