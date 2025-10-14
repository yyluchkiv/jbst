package jbst.iam.settings;

import jbst.foundation.repositories.postgres.PostgresJbstSettingsRepository;

public class PostgresJbstSettingsService extends AbstractJbstSettingsService {

    public PostgresJbstSettingsService(
            PostgresJbstSettingsRepository settingsRepository
    ) {
        super(settingsRepository);
    }
}
