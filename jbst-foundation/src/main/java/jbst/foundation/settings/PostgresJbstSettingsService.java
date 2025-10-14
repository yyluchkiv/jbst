package jbst.foundation.settings;

import jbst.foundation.repositories.postgres.PostgresJbstSettingsRepository;

public class PostgresJbstSettingsService extends JbstSettingsService {

    public PostgresJbstSettingsService(
            PostgresJbstSettingsRepository settingsRepository
    ) {
        super(settingsRepository);
    }
}
