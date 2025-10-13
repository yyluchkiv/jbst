package jbst.iam.repositories.postgres;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.properties.settings.JbstSettingsHardwareMonitoringThresholds;
import jbst.iam.domain.db.JbstSettings;
import jbst.iam.domain.postgres.db.PostgresDbJbstSettings;
import jbst.iam.repositories.JbstSettingsRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostgresJbstSettingsRepository extends JpaRepository<PostgresDbJbstSettings, UUID>, JbstSettingsRepository {
    // ================================================================================================================
    // Any
    // ================================================================================================================
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
