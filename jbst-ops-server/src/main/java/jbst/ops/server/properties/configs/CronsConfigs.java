package jbst.ops.server.properties.configs;

import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.Cron;
import jbst.foundation.domain.properties.configs.AbstractPropertiesConfigs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class CronsConfigs extends AbstractPropertiesConfigs {
    @MandatoryProperty
    private final Cron serversOrFsNotificationCron;
    @MandatoryProperty
    private final Cron fsAnyProblemsNotificationCron;

    @Override
    public boolean isParentPropertiesNode() {
        return false;
    }
}
