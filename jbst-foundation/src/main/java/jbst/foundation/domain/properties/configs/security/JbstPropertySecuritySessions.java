package jbst.foundation.domain.properties.configs.security;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
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
public class JbstPropertySecuritySessions extends JbstProperty {
    @JbstPropertyMandatory
    private final JbstPropertyCron cleanSessionsByExpiredRefreshTokensCron;
    @JbstPropertyMandatory
    private final JbstPropertyCron enableSessionsMetadataRenewCron;

    public static JbstPropertySecuritySessions hardcoded() {
        return new JbstPropertySecuritySessions(
                JbstPropertyCron.enabled("*/30 * * * * *", UKRAINE.getId()),
                JbstPropertyCron.enabled("*/15 * * * * *", UKRAINE.getId())
        );
    }

    public static JbstPropertySecuritySessions random() {
        return new JbstPropertySecuritySessions(
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
        return "sessions";
    }
}
