package jbst.foundation.settings;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.databases.JbstSettings;
import jbst.foundation.domain.dto.requests.RequestJbstSettings;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.UserOnInit;
import jbst.foundation.domain.settings.JbstSettingsHardwareMonitoringThresholds;
import jbst.foundation.repositories.JbstInvitationsRepository;
import jbst.foundation.repositories.JbstSettingsRepository;
import jbst.foundation.repositories.JbstUsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static jbst.foundation.domain.asserts.Asserts.assertTrueOrThrow;
import static jbst.foundation.domain.enums.Status.COMPLETED;
import static jbst.foundation.domain.enums.Status.STARTED;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.contactDevelopmentTeam;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.invalidAttribute;
import static jbst.foundation.utilities.spring.SpringAuthoritiesUtility.getSimpleGrantedAuthorities;

@SuppressWarnings("LoggingSimilarMessage")
@Slf4j
public abstract class JbstSettingsService {
    // Repository
    protected final JbstSettingsRepository settingsRepository;
    protected final JbstInvitationsRepository invitationsRepository;
    protected final JbstUsersRepository usersRepository;
    // Properties
    protected final JbstProperties jbstProperties;

    // Store
    private final AtomicReference<JbstSettings> settingsAR;

    public JbstSettingsService(JbstSettingsRepository settingsRepository, JbstInvitationsRepository invitationsRepository, JbstUsersRepository usersRepository, JbstProperties jbstProperties) {
        this.settingsRepository = settingsRepository;
        this.invitationsRepository = invitationsRepository;
        this.usersRepository = usersRepository;
        this.jbstProperties = jbstProperties;
        this.settingsAR = new AtomicReference<>();
    }

    abstract public long initUsers(List<UserOnInit> usersOnInit);
    abstract public void initInvitations(UserOnInit userOnInit, Set<SimpleGrantedAuthority> authorities);

    // ================================================================================================================
    // Settings: [jbst_setting] table/collection
    // ================================================================================================================
    public final void initSettings() {
        LOGGER.info(JbstConstants.Logs.PREFIX_SETTINGS + " storage initialization — {}", STARTED.asANSI());
        assertTrueOrThrow(
                this.settingsRepository.isPresent(),
                contactDevelopmentTeam("jbst-setting initialization failure")
        );
        this.settingsAR.set(
                this.settingsRepository.getSettings()
        );
        LOGGER.info(JbstConstants.Logs.PREFIX_SETTINGS + " storage initialization — {}", COMPLETED.asANSI());
    }

    @SuppressWarnings("LoggingSimilarMessage")
    public final void initUsers() {
        var essenceConfigs = this.jbstProperties.getSecurityJwtConfigs().getEssenceConfigs();
        assertTrueOrThrow(
                essenceConfigs.getUsersOnInit().isEnabled(),
                invalidAttribute("essence-configs.users-on-init.enabled == true")
        );
        if (this.usersRepository.count() == 0L) {
            LOGGER.info(JbstConstants.Logs.PREFIX + " Essence 'users-on-init' — adding users to database");
            var usersCount = this.initUsers(essenceConfigs.getUsersOnInit().getUsers());
            LOGGER.info(JbstConstants.Logs.PREFIX + " Essence 'users-on-init' — saved users: {}", usersCount);
        }
        LOGGER.info(JbstConstants.Logs.PREFIX + " Essence 'users-on-init' — {}", COMPLETED.asANSI());
    }

    public final void initInvitations() {
        var securityJwtConfigs = this.jbstProperties.getSecurityJwtConfigs();
        var essenceConfigs = securityJwtConfigs.getEssenceConfigs();
        assertTrueOrThrow(
                essenceConfigs.getInvitationsOnInit().isEnabled(),
                invalidAttribute("essenceConfigs.invitations-on-init.enabled == true")
        );
        var authorities = getSimpleGrantedAuthorities(securityJwtConfigs.getAuthoritiesConfigs().getAvailableAuthorities());
        essenceConfigs.getUsersOnInit().getUsers().forEach(userOnInit -> {
            var username = userOnInit.getUsername();
            if (this.invitationsRepository.countByOwner(username) == 0L) {
                LOGGER.info(JbstConstants.Logs.PREFIX + " Essence 'invitations-on-init' — add invitations, username: {}", username);
                this.initInvitations(userOnInit, authorities);
            }
        });
        LOGGER.info(JbstConstants.Logs.PREFIX + " Essence 'invitations-on-init' — {}", COMPLETED.asANSI());
    }

    public final JbstSettings getSettings() {
        return this.settingsAR.get();
    }

    public final void saveSettings(Username updatedBy, RequestJbstSettings request) {
        var jbstSettings = this.settingsRepository.saveAs(
                updatedBy,
                request
        );
        this.settingsAR.set(jbstSettings);
    }

    // ================================================================================================================
    // Hardware Monitoring Thresholds
    // ================================================================================================================
    public final boolean isHardwareMonitoringThresholdsEnabled() {
        return this.settingsAR.get().getHardwareMonitoringThresholds().enabled();
    }

    public JbstSettingsHardwareMonitoringThresholds getHardwareMonitoringThresholds() {
        return this.settingsAR.get().getHardwareMonitoringThresholds();
    }
}
