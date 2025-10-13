package jbst.iam.repositories.mongodb;

import jbst.foundation.domain.base.Username;
import jbst.iam.domain.db.JbstSettings;
import jbst.iam.domain.mongodb.MongoDbJbstSettings;
import jbst.iam.domain.settings.JbstSettingsHardwareMonitoringThresholds;
import jbst.iam.repositories.JbstSettingsRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.contactDevelopmentTeam;

public interface MongoJbstSettingsRepository extends MongoRepository<MongoDbJbstSettings, String>, JbstSettingsRepository {
    // ================================================================================================================
    // Any
    // ================================================================================================================
    default JbstSettings getSettings() {
        return this.findAll().stream()
                .findFirst()
                .map(MongoDbJbstSettings::jbstSettings)
                .orElseThrow(() -> new IllegalArgumentException(contactDevelopmentTeam("No jbst settings")));
    }

    default boolean isPresent() {
        return this.count() > 0;
    }

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
