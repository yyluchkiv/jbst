package jbst.foundation.settings;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstSettings;
import jbst.foundation.domain.dto.requests.RequestJbstSettings;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.JbstPropertyUserOnInit;
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
import static jbst.foundation.domain.constants.JbstConstants.Logs.PREFIX;
import static jbst.foundation.domain.enums.JbstStatus.COMPLETED;
import static jbst.foundation.domain.enums.JbstStatus.STARTED;
import static jbst.foundation.domain.strings.JbstMessages.contactDevelopmentTeam;
import static jbst.foundation.domain.strings.JbstMessages.invalidAttribute;
import static jbst.foundation.domain.spring.JbstSpringAuthorities.getSimpleGrantedAuthorities;

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
    protected final AtomicReference<JbstSettings> settingsAR;

    public JbstSettingsService(JbstSettingsRepository settingsRepository, JbstInvitationsRepository invitationsRepository, JbstUsersRepository usersRepository, JbstProperties jbstProperties) {
        this.settingsRepository = settingsRepository;
        this.invitationsRepository = invitationsRepository;
        this.usersRepository = usersRepository;
        this.jbstProperties = jbstProperties;
        this.settingsAR = new AtomicReference<>();
    }

    abstract public long initUsers(List<JbstPropertyUserOnInit> usersOnInit);
    abstract public void initInvitations(JbstPropertyUserOnInit userOnInit, Set<SimpleGrantedAuthority> authorities);

    // ================================================================================================================
    // Settings: [jbst_setting] table/collection
    // ================================================================================================================
    public void initSettings() {
        LOGGER.info("{} settings storage initialization — {}", PREFIX, STARTED.asANSI());
        assertTrueOrThrow(
                this.settingsRepository.isPresent(),
                contactDevelopmentTeam("jbst-setting initialization failure")
        );
        this.settingsAR.set(
                this.settingsRepository.getSettings()
        );
        LOGGER.info("{} settings storage initialization — {}", PREFIX, COMPLETED.asANSI());
    }

    @SuppressWarnings("LoggingSimilarMessage")
    public void initUsers() {
        var essenceConfigs = this.jbstProperties.getSecurity().getEssence();
        assertTrueOrThrow(
                essenceConfigs.getUsersOnInit().isEnabled(),
                invalidAttribute("essence-configs.users-on-init.enabled == true")
        );
        if (this.usersRepository.count() == 0L) {
            LOGGER.info("{} essence 'users-on-init' — adding users to database", PREFIX);
            var usersCount = this.initUsers(essenceConfigs.getUsersOnInit().getUsers());
            LOGGER.info("{} essence 'users-on-init' — saved users: {}", PREFIX, usersCount);
        }
        LOGGER.info("{} essence 'users-on-init' — {}", PREFIX, COMPLETED.asANSI());
    }

    public void initInvitations() {
        var security = this.jbstProperties.getSecurity();
        var essence = security.getEssence();
        assertTrueOrThrow(
                essence.getInvitationsOnInit().isEnabled(),
                invalidAttribute("essence-configs.invitations-on-init.enabled == true")
        );
        var authorities = getSimpleGrantedAuthorities(security.getAuthorities().getAvailableAuthorities());
        essence.getUsersOnInit().getUsers().forEach(userOnInit -> {
            var username = userOnInit.getUsername();
            if (this.invitationsRepository.countByOwner(username) == 0L) {
                LOGGER.info("{} essence 'invitations-on-init' — add invitations, username: {}", PREFIX, username);
                this.initInvitations(userOnInit, authorities);
            }
        });
        LOGGER.info("{} essence 'invitations-on-init' — {}", PREFIX, COMPLETED.asANSI());
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
        return this.settingsAR.get().hardwareMonitoringThresholds().enabled();
    }

    public JbstSettingsHardwareMonitoringThresholds getHardwareMonitoringThresholds() {
        return this.settingsAR.get().hardwareMonitoringThresholds();
    }
}
