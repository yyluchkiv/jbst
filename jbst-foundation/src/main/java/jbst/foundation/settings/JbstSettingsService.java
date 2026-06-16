package jbst.foundation.settings;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstSettings;
import jbst.foundation.domain.dto.requests.JbstRequestJbstSettings;
import jbst.foundation.domain.settings.JbstSettingsHardwareMonitoringThresholds;
import jbst.foundation.repositories.JbstSettingsRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

import static jbst.foundation.domain.asserts.JbstAsserts.assertTrueOrThrow;
import static jbst.foundation.domain.constants.JbstConstants.Logs.PREFIX;
import static jbst.foundation.domain.enums.JbstStatus.COMPLETED;
import static jbst.foundation.domain.enums.JbstStatus.STARTED;
import static jbst.foundation.domain.strings.JbstMessages.contactDevelopmentTeam;

@SuppressWarnings("LoggingSimilarMessage")
@Slf4j
public abstract class JbstSettingsService {
    // Repository
    protected final JbstSettingsRepository settingsRepository;

    // Store
    protected final AtomicReference<JbstSettings> settingsAR;

    public JbstSettingsService(JbstSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
        this.settingsAR = new AtomicReference<>();
    }

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

    public final JbstSettings getSettings() {
        return this.settingsAR.get();
    }

    public final void saveSettings(Username updatedBy, JbstRequestJbstSettings request) {
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
