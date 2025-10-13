package jbst.iam.settings;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.iam.domain.db.JbstSettings;
import jbst.iam.repositories.JbstSettingsRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

import static jbst.foundation.domain.enums.Status.COMPLETED;
import static jbst.foundation.domain.enums.Status.STARTED;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.contactDevelopmentTeam;

@SuppressWarnings("LoggingSimilarMessage")
@Slf4j
public abstract class AbstractJbstSettingsService {
    // Repository
    private final JbstSettingsRepository jbstSettingsRepository;

    // Store
    private final AtomicReference<JbstSettings> jbstSettingsAR;

    public AbstractJbstSettingsService(JbstSettingsRepository jbstSettingsRepository) {
        this.jbstSettingsRepository = jbstSettingsRepository;
        this.jbstSettingsAR = new AtomicReference<>();
    }

    // ================================================================================================================
    // ALL
    // ================================================================================================================
    public final void initialize() {
        LOGGER.info(JbstConstants.Logs.PREFIX_SETTINGS + " storage initialization — {}", STARTED.asANSI());
        if (this.jbstSettingsRepository.isPresent()) {
            this.jbstSettingsAR.set(
                    this.jbstSettingsRepository.getSettings()
            );
        } else {
            throw new IllegalArgumentException(contactDevelopmentTeam("jbst-setting initialization failure"));
        }
        LOGGER.info(JbstConstants.Logs.PREFIX_SETTINGS + " storage initialization — {}", COMPLETED.asANSI());
    }

    public final JbstSettings getSettings() {
        return this.jbstSettingsAR.get();
    }

    // ================================================================================================================
    // Hardware Monitoring Thresholds
    // ================================================================================================================
    public final boolean isHardwareMonitoringThresholdsEnabled() {
        return this.jbstSettingsAR.get().hardwareMonitoringThresholds().enabled();
    }
}
