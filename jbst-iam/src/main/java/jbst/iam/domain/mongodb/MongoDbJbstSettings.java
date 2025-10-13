package jbst.iam.domain.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jbst.foundation.domain.base.Username;
import jbst.iam.domain.db.JbstSettings;
import jbst.iam.domain.settings.JbstSettingsHardwareMonitoringThresholds;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import static jbst.foundation.utilities.time.TimestampUtility.getCurrentTimestamp;

// Lombok
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
// Mongodb
@Document(collection = MongoDbJbstSettings.MONGO_TABLE_NAME)
public class MongoDbJbstSettings {
    public static final String MONGO_TABLE_NAME = "jbst_settings";

    @Id
    private String id;
    private Username createdBy;
    private long createdAt;
    private Username updatedBy;
    private long updatedAt;
    private JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds;

    public MongoDbJbstSettings(
            Username username,
            JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds
    ) {
        this.createdBy = username;
        this.updatedBy = username;
        var currentTimestamp = getCurrentTimestamp();
        this.createdAt = currentTimestamp;
        this.updatedAt = currentTimestamp;
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
