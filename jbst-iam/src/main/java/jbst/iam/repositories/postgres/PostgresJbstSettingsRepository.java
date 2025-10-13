package jbst.iam.repositories.postgres;

import jbst.foundation.domain.base.Username;
import jbst.iam.domain.db.JbstSettings;
import jbst.iam.domain.dto.requests.RequestJbstSettings;
import jbst.iam.domain.postgres.db.PostgresDbJbstSettings;
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
            Username updatedBy,
            RequestJbstSettings request
    ) {
        var entity = this.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException(contactDevelopmentTeam("No jbst settings")));
        entity.edit(
                updatedBy,
                request.hardwareMonitoringThresholds()
        );
        this.save(entity);
        return entity.jbstSettings();
    }
}
