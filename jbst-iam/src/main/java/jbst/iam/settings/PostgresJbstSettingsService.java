package jbst.iam.settings;

import jbst.foundation.domain.properties.JbstSettingsOnInit;
import jbst.iam.repositories.postgres.PostgresJbstSettingsRepository;

public class PostgresJbstSettingsService extends AbstractJbstSettingsService {

    public PostgresJbstSettingsService(
            JbstSettingsOnInit jbstSettingsOnInit,
            PostgresJbstSettingsRepository settingsRepository
    ) {
        super(jbstSettingsOnInit, settingsRepository);
    }
}
