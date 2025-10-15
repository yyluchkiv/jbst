package jbst.foundation.settings;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.databases.JbstSettings;
import jbst.foundation.domain.dto.requests.RequestJbstSettings;
import jbst.foundation.domain.settings.JbstSettingsHardwareMonitoringThresholds;
import jbst.foundation.repositories.JbstSettingsRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

import static jbst.foundation.domain.asserts.Asserts.assertTrueOrThrow;
import static jbst.foundation.domain.enums.Status.COMPLETED;
import static jbst.foundation.domain.enums.Status.STARTED;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.contactDevelopmentTeam;

@SuppressWarnings("LoggingSimilarMessage")
@Slf4j
public abstract class JbstSettingsService {
    // Repository
    private final JbstSettingsRepository settingsRepository;

    // Store
    private final AtomicReference<JbstSettings> settingsAR;

    public JbstSettingsService(JbstSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
        this.settingsAR = new AtomicReference<>();
    }

    // ================================================================================================================
    // Settings: [jbst_setting] table/collection
    // ================================================================================================================
    public final void initializeSettings() {
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
