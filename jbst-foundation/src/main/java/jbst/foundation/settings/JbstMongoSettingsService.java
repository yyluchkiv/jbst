package jbst.foundation.settings;

import jbst.foundation.repositories.mongo.JbstMongoSettingsRepository;

public class JbstMongoSettingsService extends JbstSettingsService {

    public JbstMongoSettingsService(
            JbstMongoSettingsRepository settingsRepository
    ) {
        super(settingsRepository);
    }
}
