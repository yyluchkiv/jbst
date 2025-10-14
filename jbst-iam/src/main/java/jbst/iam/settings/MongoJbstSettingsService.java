package jbst.iam.settings;

import jbst.foundation.repositories.mongo.MongoJbstSettingsRepository;

public class MongoJbstSettingsService extends AbstractJbstSettingsService {

    public MongoJbstSettingsService(
            MongoJbstSettingsRepository settingsRepository
    ) {
        super(settingsRepository);
    }
}
