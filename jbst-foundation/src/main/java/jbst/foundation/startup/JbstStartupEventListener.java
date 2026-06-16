package jbst.foundation.startup;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.enums.JbstStatus;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.services.JbstInvitationsService;
import jbst.foundation.services.JbstUsersService;
import jbst.foundation.settings.JbstSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import static jbst.foundation.domain.constants.JbstConstants.Logs.PREFIX;
import static jbst.foundation.domain.enums.JbstStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstStartupEventListener {

    // Settings
    protected final JbstSettingsService settingsService;
    protected final JbstUsersService usersService;
    protected final JbstInvitationsService invitationsService;
    // Properties
    protected final JbstProperties jbstProperties;

    @EventListener(ApplicationStartedEvent.class)
    public void onStartup() {
        LOGGER.info(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
        LOGGER.info(JbstConstants.Logs.getServerStartup(this.jbstProperties.getApp(), STARTED));

        this.settingsService.initSettings();
        LOGGER.info(JbstConstants.Logs.getServerStartup(this.jbstProperties.getApp(), PROGRESS_33));

        var users = this.jbstProperties.getSecurity().getEssence().getUsersOnInit();
        LOGGER.info("{} essence 'users-on-init' — {}", PREFIX, JbstStatus.of(users.isEnabled()).asANSI());
        if (users.isEnabled()) {
            this.usersService.initUsers();
        }
        LOGGER.info(JbstConstants.Logs.getServerStartup(this.jbstProperties.getApp(), PROGRESS_66));

        var invitations = this.jbstProperties.getSecurity().getEssence().getInvitationsOnInit();
        LOGGER.info("{} essence 'invitations-on-init' — {}", PREFIX, JbstStatus.of(invitations.isEnabled()).asANSI());
        if (invitations.isEnabled()) {
            this.invitationsService.initInvitations();
        }
        LOGGER.info(JbstConstants.Logs.getServerStartup(this.jbstProperties.getApp(), PROGRESS_99));

        LOGGER.info(JbstConstants.Logs.getServerStartup(this.jbstProperties.getApp(), COMPLETED));
        LOGGER.info(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
    }
}
