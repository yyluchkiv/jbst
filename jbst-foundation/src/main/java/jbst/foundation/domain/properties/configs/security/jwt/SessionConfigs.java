package jbst.foundation.domain.properties.configs.security.jwt;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.JbstPropertyCron;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class SessionConfigs extends JbstProperty {
    @MandatoryProperty
    private final JbstPropertyCron cleanSessionsByExpiredRefreshTokensCron;
    @MandatoryProperty
    private final JbstPropertyCron enableSessionsMetadataRenewCron;

    public static SessionConfigs hardcoded() {
        return new SessionConfigs(
                JbstPropertyCron.enabled("*/30 * * * * *", UKRAINE.getId()),
                JbstPropertyCron.enabled("*/15 * * * * *", UKRAINE.getId())
        );
    }

    public static SessionConfigs random() {
        return new SessionConfigs(
                JbstPropertyCron.random(),
                JbstPropertyCron.random()
        );
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.BRANCH;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonLeaf() {
        return "session-configs";
    }
}
