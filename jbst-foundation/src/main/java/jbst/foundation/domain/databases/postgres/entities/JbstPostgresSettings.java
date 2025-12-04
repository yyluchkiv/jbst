package jbst.foundation.domain.databases.postgres.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstSettings;
import jbst.foundation.domain.databases.postgres.superclasses.JbstPostgresAbstractPersistableAuditableUUID;
import jbst.foundation.domain.settings.JbstSettingsHardwareMonitoringThresholds;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.Transient;

// Lombok
@SuppressWarnings("JpaDataSourceORMInspection")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
// JPA
@Entity
@Table(name = JbstPostgresSettings.PG_TABLE_NAME)
public class JbstPostgresSettings extends JbstPostgresAbstractPersistableAuditableUUID {
    public static final String PG_TABLE_NAME = "jbst_settings";

    @Type(JsonBinaryType.class)
    @Column(name = "hardware_monitoring_thresholds", nullable = false)
    private JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds;

    public JbstPostgresSettings() {
        // ignored, JPA-required
    }

    public void edit(Username updatedBy, JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds) {
        this.update(updatedBy);
        this.hardwareMonitoringThresholds = hardwareMonitoringThresholds;
    }

    @JsonIgnore
    @Transient
    public JbstSettings jbstSettings() {
        return new JbstSettings(
                this.getCreatedUTC(),
                this.getUpdatedUTC(),
                this.hardwareMonitoringThresholds
        );
    }
}
