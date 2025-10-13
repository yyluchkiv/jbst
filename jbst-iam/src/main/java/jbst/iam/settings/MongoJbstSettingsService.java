package jbst.iam.settings;

import jbst.iam.repositories.mongodb.MongoJbstSettingsRepository;

public class MongoJbstSettingsService extends AbstractJbstSettingsService {

    public MongoJbstSettingsService(
            MongoJbstSettingsRepository settingsRepository
    ) {
        super(settingsRepository);
    }
}
