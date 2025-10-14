package jbst.foundation.repositories.postgres;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstSettings;
import jbst.foundation.domain.dto.requests.RequestJbstSettings;
import jbst.foundation.domain.databases.postgres.entities.PostgresDbJbstSettings;
import jbst.foundation.repositories.JbstSettingsRepository;
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
