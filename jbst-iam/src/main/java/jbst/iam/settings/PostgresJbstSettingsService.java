package jbst.iam.settings;

import jbst.iam.repositories.postgres.PostgresJbstSettingsRepository;

public class PostgresJbstSettingsService extends AbstractJbstSettingsService {

    public PostgresJbstSettingsService(
            PostgresJbstSettingsRepository settingsRepository
    ) {
        super(settingsRepository);
    }
}
