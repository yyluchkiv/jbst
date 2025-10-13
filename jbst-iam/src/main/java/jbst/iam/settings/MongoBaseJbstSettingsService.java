package jbst.iam.settings;

import jbst.foundation.domain.properties.JbstSettingsOnInit;
import jbst.iam.repositories.mongodb.MongoJbstSettingsRepository;

public class MongoBaseJbstSettingsService extends AbstractJbstSettingsService {

    public MongoBaseJbstSettingsService(
            JbstSettingsOnInit jbstSettingsOnInit,
            MongoJbstSettingsRepository settingsRepository
    ) {
        super(jbstSettingsOnInit, settingsRepository);
    }
}
