package jbst.foundation.startup;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.enums.Status;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.settings.JbstSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import static jbst.foundation.domain.constants.JbstConstants.Logs.PREFIX;
import static jbst.foundation.domain.enums.Status.*;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstStartupEventListener {

    // Settings
    protected final JbstSettingsService settingsService;
    // Properties
    protected final JbstProperties jbstProperties;

    @EventListener(ApplicationStartedEvent.class)
    public void onStartup() {
        LOGGER.info(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
        LOGGER.info(JbstConstants.Logs.getServerStartup(this.jbstProperties.getApp(), STARTED));

        this.settingsService.initSettings();
        LOGGER.info(JbstConstants.Logs.getServerStartup(this.jbstProperties.getApp(), PROGRESS_33));

        var users = this.jbstProperties.getSecurity().getEssenceConfigs().getUsersOnInit();
        LOGGER.info("{} essence 'users-on-init' — {}", PREFIX, Status.of(users.isEnabled()).asANSI());
        if (users.isEnabled()) {
            this.settingsService.initUsers();
        }
        LOGGER.info(JbstConstants.Logs.getServerStartup(this.jbstProperties.getApp(), PROGRESS_66));

        var invitations = this.jbstProperties.getSecurity().getEssenceConfigs().getInvitationsOnInit();
        LOGGER.info("{} essence 'invitations-on-init' — {}", PREFIX, Status.of(invitations.isEnabled()).asANSI());
        if (invitations.isEnabled()) {
            this.settingsService.initInvitations();
        }
        LOGGER.info(JbstConstants.Logs.getServerStartup(this.jbstProperties.getApp(), PROGRESS_99));

        LOGGER.info(JbstConstants.Logs.getServerStartup(this.jbstProperties.getApp(), COMPLETED));
        LOGGER.info(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
    }
}
