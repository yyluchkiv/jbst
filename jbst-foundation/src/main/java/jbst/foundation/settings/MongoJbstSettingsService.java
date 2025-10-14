package jbst.foundation.settings;

import jbst.foundation.repositories.mongo.MongoJbstSettingsRepository;

public class MongoJbstSettingsService extends JbstSettingsService {

    public MongoJbstSettingsService(
            MongoJbstSettingsRepository settingsRepository
    ) {
        super(settingsRepository);
    }
}
