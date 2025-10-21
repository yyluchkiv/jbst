package jbst.foundation.domain.properties.configs.security.jwt;

import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.Cron;
import jbst.foundation.domain.properties.configs.AbstractPropertiesConfigs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class SessionConfigs extends AbstractPropertiesConfigs {
    @MandatoryProperty
    private final Cron cleanSessionsByExpiredRefreshTokensCron;
    @MandatoryProperty
    private final Cron enableSessionsMetadataRenewCron;

    public static SessionConfigs hardcoded() {
        return new SessionConfigs(
                Cron.enabled("*/30 * * * * *", UKRAINE.getId()),
                Cron.enabled("*/15 * * * * *", UKRAINE.getId())
        );
    }

    public static SessionConfigs random() {
        return new SessionConfigs(
                Cron.random(),
                Cron.random()
        );
    }

    @Override
    public boolean isParentPropertiesNode() {
        return false;
    }

    @Override
    public PropertyId getPropertyId() {
        return new PropertyId("session-configs");
    }
}
