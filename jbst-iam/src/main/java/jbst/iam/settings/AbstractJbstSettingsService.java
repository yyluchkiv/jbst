package jbst.iam.settings;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstSettingsOnInit;
import jbst.iam.domain.db.JbstSettings;
import jbst.iam.repositories.JbstSettingsRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

import static jbst.foundation.domain.enums.Status.COMPLETED;
import static jbst.foundation.domain.enums.Status.STARTED;

@SuppressWarnings("LoggingSimilarMessage")
@Slf4j
public abstract class AbstractJbstSettingsService {
    private static final String INIT_LOG = JbstConstants.Logs.PREFIX_SETTINGS + " yml ↔ database synchronization — {}";

    // Properties
    private final JbstSettingsOnInit jbstSettingsOnInit;
    // Repositories Abstraction
    private final JbstSettingsRepository jbstSettingsRepository;

    // database abstraction ↔ memory
    private final AtomicReference<JbstSettings> jbstSettingsAR;

    public AbstractJbstSettingsService(JbstSettingsOnInit jbstSettingsOnInit, JbstSettingsRepository jbstSettingsRepository) {
        this.jbstSettingsOnInit = jbstSettingsOnInit;
        this.jbstSettingsRepository = jbstSettingsRepository;
        this.jbstSettingsAR = new AtomicReference<>();
    }

    public final void saveOnInit() {
        LOGGER.info(INIT_LOG, STARTED.asANSI());
        if (this.jbstSettingsRepository.isPresent()) {
            this.jbstSettingsAR.set(
                    this.jbstSettingsRepository.getSettings()
            );
            LOGGER.info(INIT_LOG, "settings already present");
        } else {
            LOGGER.info(INIT_LOG, "no settings");
            this.jbstSettingsAR.set(
                    this.jbstSettingsRepository.saveAs(
                            Username.ops(),
                            this.jbstSettingsOnInit.getHardwareMonitoringThresholds()
                    )
            );
            LOGGER.info(INIT_LOG, "settings saved");
        }
        LOGGER.info(INIT_LOG, COMPLETED.asANSI());

        System.out.println("====");
        System.out.println(this.jbstSettingsAR.get());
        System.out.println("====");
    }
}
