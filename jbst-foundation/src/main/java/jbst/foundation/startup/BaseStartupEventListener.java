package jbst.foundation.startup;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.enums.Status;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.essense.AbstractEssenceConstructor;
import jbst.foundation.settings.JbstSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static jbst.foundation.domain.enums.Status.*;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BaseStartupEventListener implements AbstractServerStartupEventListener {

    // Settings
    protected final JbstSettingsService jbstSettingsService;
    // Essence
    protected final AbstractEssenceConstructor essenceConstructor;
    // Properties
    protected final JbstProperties jbstProperties;

    @Override
    public void onStartup() {
        LOGGER.info(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
        LOGGER.info(JbstConstants.Logs.getServerStartup(this.jbstProperties.getServerConfigs(), STARTED));

        this.jbstSettingsService.initializeSettings();
        LOGGER.info(JbstConstants.Logs.getServerStartup(this.jbstProperties.getServerConfigs(), PROGRESS_33));

        var defaultUsers = this.jbstProperties.getSecurityJwtConfigs().getEssenceConfigs().getDefaultUsers();
        LOGGER.info("{} Essence 'default-users' — {}", JbstConstants.Logs.PREFIX, Status.of(defaultUsers.isEnabled()).asANSI());
        if (defaultUsers.isEnabled()) {
            this.essenceConstructor.addDefaultUsers();
        }
        LOGGER.info(JbstConstants.Logs.getServerStartup(this.jbstProperties.getServerConfigs(), PROGRESS_66));

        var invitations = this.jbstProperties.getSecurityJwtConfigs().getEssenceConfigs().getInvitations();
        LOGGER.info("{} Essence 'invitations' — {}", JbstConstants.Logs.PREFIX, Status.of(invitations.isEnabled()).asANSI());
        if (invitations.isEnabled()) {
            this.essenceConstructor.addDefaultUsersInvitations();
        }
        LOGGER.info(JbstConstants.Logs.getServerStartup(this.jbstProperties.getServerConfigs(), PROGRESS_99));

        LOGGER.info(JbstConstants.Logs.getServerStartup(this.jbstProperties.getServerConfigs(), COMPLETED));
        LOGGER.info(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
    }
}
