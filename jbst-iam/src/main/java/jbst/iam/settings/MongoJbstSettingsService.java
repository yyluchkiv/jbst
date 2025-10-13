package jbst.iam.settings;

import jbst.foundation.domain.properties.JbstSettingsOnInit;
import jbst.iam.repositories.mongodb.MongoJbstSettingsRepository;

public class MongoJbstSettingsService extends AbstractJbstSettingsService {

    public MongoJbstSettingsService(
            JbstSettingsOnInit jbstSettingsOnInit,
            MongoJbstSettingsRepository settingsRepository
    ) {
        super(jbstSettingsOnInit, settingsRepository);
    }
}
