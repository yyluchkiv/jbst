package jbst.foundation.settings;

import jbst.foundation.repositories.postgres.JbstPostgresSettingsRepository;

public class JbstPostgresSettingsService extends JbstSettingsService {

    public JbstPostgresSettingsService(
            JbstPostgresSettingsRepository settingsRepository
    ) {
        super(settingsRepository);
    }
}
