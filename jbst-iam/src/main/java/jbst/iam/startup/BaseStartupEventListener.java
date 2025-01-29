package jbst.iam.startup;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.enums.Status;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.iam.essence.AbstractEssenceConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static jbst.foundation.domain.enums.Status.COMPLETED;
import static jbst.foundation.domain.enums.Status.STARTED;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BaseStartupEventListener implements AbstractServerStartupEventListener {

    // Essence
    protected final AbstractEssenceConstructor essenceConstructor;
    // Properties
    protected final JbstProperties jbstProperties;

    @Override
    public void onStartup() {
        LOGGER.info(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
        LOGGER.info(JbstConstants.Logs.getServerStartup(this.jbstProperties.getServerConfigs(), STARTED));

        var defaultUsers = this.jbstProperties.getSecurityJwtConfigs().getEssenceConfigs().getDefaultUsers();
        LOGGER.info("{} Essence 'default-users' — {}", JbstConstants.Logs.PREFIX, Status.of(defaultUsers.isEnabled()).asANSI());
        if (defaultUsers.isEnabled()) {
            this.essenceConstructor.addDefaultUsers();
        }

        var invitations = this.jbstProperties.getSecurityJwtConfigs().getEssenceConfigs().getInvitations();
        LOGGER.info("{} Essence 'invitations' — {}", JbstConstants.Logs.PREFIX, Status.of(invitations.isEnabled()).asANSI());
        if (invitations.isEnabled()) {
            this.essenceConstructor.addDefaultUsersInvitations();
        }

        LOGGER.info(JbstConstants.Logs.getServerStartup(this.jbstProperties.getServerConfigs(), COMPLETED));
        LOGGER.info(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
    }
}
