package jbst.iam.settings;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstSettingsOnInit;
import jbst.iam.repositories.JbstSettingsRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static jbst.foundation.domain.enums.Status.COMPLETED;
import static jbst.foundation.domain.enums.Status.STARTED;

@SuppressWarnings("LoggingSimilarMessage")
@Slf4j
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractJbstSettingsService {
    private static final String INIT_LOG = JbstConstants.Logs.PREFIX_SETTINGS + " yml ↔ database synchronization — {}";

    // Properties
    private final JbstSettingsOnInit jbstSettingsOnInit;
    // Repositories on $DATABASE
    private final JbstSettingsRepository jbstSettingsRepository;

    public final void saveOnInit() {
        LOGGER.info(INIT_LOG, STARTED.asANSI());
        if (this.jbstSettingsRepository.count() == 0L) {
            LOGGER.info(INIT_LOG, "no settings");
            this.jbstSettingsRepository.saveAs(
                    Username.ops(),
                    this.jbstSettingsOnInit.getHardwareMonitoringThresholds()
            );
            LOGGER.info(INIT_LOG, "settings saved");
        } else {
            LOGGER.info(INIT_LOG, "settings already present");
        }
        LOGGER.info(INIT_LOG, COMPLETED.asANSI());
    }
}
