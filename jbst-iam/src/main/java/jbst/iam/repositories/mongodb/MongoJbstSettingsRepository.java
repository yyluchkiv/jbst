package jbst.iam.repositories.mongodb;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.properties.settings.JbstSettingsHardwareMonitoringThresholds;
import jbst.iam.domain.db.JbstSettings;
import jbst.iam.domain.mongodb.MongoDbJbstSettings;
import jbst.iam.repositories.JbstSettingsRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoJbstSettingsRepository extends MongoRepository<MongoDbJbstSettings, String>, JbstSettingsRepository {
    // ================================================================================================================
    // Any
    // ================================================================================================================
    default JbstSettings saveAs(
            Username username,
            JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds
    ) {
        var entity = this.save(new MongoDbJbstSettings(
                username,
                hardwareMonitoringThresholds
        ));
        return entity.jbstSettings();
    }
}
