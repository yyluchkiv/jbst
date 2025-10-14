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
    private final JbstSettingsRepository jbstSettingsRepository;

    // Store
    private final AtomicReference<JbstSettings> jbstSettingsAR;

    public JbstSettingsService(JbstSettingsRepository jbstSettingsRepository) {
        this.jbstSettingsRepository = jbstSettingsRepository;
        this.jbstSettingsAR = new AtomicReference<>();
    }

    // ================================================================================================================
    // Settings: [jbst_setting] table/collection
    // ================================================================================================================
    public final void initializeSettings() {
        LOGGER.info(JbstConstants.Logs.PREFIX_SETTINGS + " storage initialization — {}", STARTED.asANSI());
        assertTrueOrThrow(
                this.jbstSettingsRepository.isPresent(),
                contactDevelopmentTeam("jbst-setting initialization failure")
        );
        this.jbstSettingsAR.set(
                this.jbstSettingsRepository.getSettings()
        );
        LOGGER.info(JbstConstants.Logs.PREFIX_SETTINGS + " storage initialization — {}", COMPLETED.asANSI());
    }

    public final JbstSettings getSettings() {
        return this.jbstSettingsAR.get();
    }

    public final void saveSettings(Username updatedBy, RequestJbstSettings request) {
        var jbstSettings = this.jbstSettingsRepository.saveAs(
                updatedBy,
                request
        );
        this.jbstSettingsAR.set(jbstSettings);
    }

    // ================================================================================================================
    // Hardware Monitoring Thresholds
    // ================================================================================================================
    public final boolean isHardwareMonitoringThresholdsEnabled() {
        return this.jbstSettingsAR.get().getHardwareMonitoringThresholds().enabled();
    }

    public JbstSettingsHardwareMonitoringThresholds getHardwareMonitoringThresholds() {
        return this.jbstSettingsAR.get().getHardwareMonitoringThresholds();
    }
}
