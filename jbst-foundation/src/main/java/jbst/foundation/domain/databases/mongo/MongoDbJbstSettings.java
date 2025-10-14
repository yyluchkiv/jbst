package jbst.foundation.domain.databases.mongo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstSettings;
import jbst.foundation.domain.hardware.monitoring.HardwareName;
import jbst.foundation.domain.settings.JbstSettingsHardwareMonitoringThresholds;
import lombok.*;
import org.bson.types.Decimal128;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.stream.Collectors;

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
    // HardwareMonitoringThreshold
    private boolean hmtEnabled;
    private Map<HardwareName, Decimal128> hmtValues;

    public void edit(
            Username updatedBy,
            JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds
    ) {
        this.updatedBy = updatedBy;
        this.updatedAt = getCurrentTimestamp();
        this.hmtEnabled = hardwareMonitoringThresholds.enabled();
        this.hmtValues = hardwareMonitoringThresholds.values().entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> new Decimal128(entry.getValue())
        ));
    }

    @JsonIgnore
    @Transient
    public JbstSettings jbstSettings() {
        var hardwareMonitoringThresholds = new JbstSettingsHardwareMonitoringThresholds(
                this.hmtEnabled,
                this.hmtValues.entrySet().stream().collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().bigDecimalValue()
                ))
        );
        return new JbstSettings(
                this.createdBy,
                this.createdAt,
                this.updatedBy,
                this.updatedAt,
                hardwareMonitoringThresholds
        );
    }
}
