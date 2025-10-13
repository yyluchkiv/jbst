package jbst.iam.settings;

import jbst.foundation.domain.properties.JbstSettingsOnInit;
import jbst.iam.repositories.postgres.PostgresJbstSettingsRepository;

public class PostgresBaseJbstSettingsService extends AbstractJbstSettingsService {

    public PostgresBaseJbstSettingsService(
            JbstSettingsOnInit jbstSettingsOnInit,
            PostgresJbstSettingsRepository settingsRepository
    ) {
        super(jbstSettingsOnInit, settingsRepository);
    }
}
