package jbst.iam.repositories.postgres;

import jbst.foundation.domain.base.Username;
import jbst.iam.domain.db.JbstSettings;
import jbst.iam.domain.postgres.db.PostgresDbJbstSettings;
import jbst.iam.domain.settings.JbstSettingsHardwareMonitoringThresholds;
import jbst.iam.repositories.JbstSettingsRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.contactDevelopmentTeam;

public interface PostgresJbstSettingsRepository extends JpaRepository<PostgresDbJbstSettings, UUID>, JbstSettingsRepository {
    // ================================================================================================================
    // Any
    // ================================================================================================================
    default JbstSettings getSettings() {
        return this.findAll().stream()
                .findFirst()
                .map(PostgresDbJbstSettings::jbstSettings)
                .orElseThrow(() -> new IllegalArgumentException(contactDevelopmentTeam("No jbst settings")));
    }

    default boolean isPresent() {
        return this.count() > 0;
    }

    default JbstSettings saveAs(
            Username username,
            JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds
    ) {
        var entity = this.save(new PostgresDbJbstSettings(
                username,
                hardwareMonitoringThresholds
        ));
        return entity.jbstSettings();
    }
}
