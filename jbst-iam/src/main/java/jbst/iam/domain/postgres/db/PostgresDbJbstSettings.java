package jbst.iam.domain.postgres.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.properties.settings.JbstSettingsHardwareMonitoringThresholds;
import jbst.iam.domain.db.JbstSettings;
import jbst.iam.domain.postgres.superclasses.PostgresDbAbstractPersistableAuditableUUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.Transient;

// Lombok
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
// JPA
@Entity
@Table(name = PostgresDbJbstSettings.PG_TABLE_NAME)
public class PostgresDbJbstSettings extends PostgresDbAbstractPersistableAuditableUUID {
    public static final String PG_TABLE_NAME = "jbst_settings";

    @Type(JsonBinaryType.class)
    @Column(name = "hardware_monitoring_thresholds", nullable = false)
    private JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds;

    public PostgresDbJbstSettings() {
        // ignored, JPA-required
    }

    public PostgresDbJbstSettings(
            Username username,
            JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds
    ) {
        super(username);
        this.hardwareMonitoringThresholds = hardwareMonitoringThresholds;
    }

    @JsonIgnore
    @Transient
    public JbstSettings jbstSettings() {
        return new JbstSettings(
                this.createdBy,
                this.createdAt,
                this.updatedBy,
                this.updatedAt,
                this.hardwareMonitoringThresholds
        );
    }
}
