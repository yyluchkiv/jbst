package jbst.foundation.domain.events;

import jbst.foundation.domain.hardware.monitoring.JbstHardwareMonitoringDatapointTableView;
import jbst.foundation.domain.system.JbstSystemResetServerStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class JbstWebsocketEvent {
    private final Map<String, Object> attributes;

    public JbstWebsocketEvent() {
        this.attributes = new HashMap<>();
    }

    public JbstWebsocketEvent(Map<String, Object> attributes) {
        this.attributes = new HashMap<>(attributes);
    }

    public static JbstWebsocketEvent hardwareMonitoring(@NotNull JbstHardwareMonitoringDatapointTableView datapoint) {
        return new JbstWebsocketEvent(
                Map.of(
                        Constants.Keys.TYPE, Constants.Values.TYPE_HARDWARE_MONITORING,
                        "datapoint", datapoint
                )
        );
    }

    public static JbstWebsocketEvent resetServerProgress(@NotNull JbstSystemResetServerStatus status) {
        return new JbstWebsocketEvent(
                Map.of(
                        Constants.Keys.TYPE, Constants.Values.TYPE_RESET_SERVER_PROGRESS,
                        "status", status
                )
        );
    }

    public void add(String key, Object value) {
        this.attributes.put(key, value);
    }

    // =================================================================================================================
    // CLASSES
    // =================================================================================================================
    @UtilityClass
    public static class Constants {
        @UtilityClass
        public static class Keys {
            public static final String TYPE = "eventType";
        }
        @UtilityClass
        public static class Values {
            public static final String TYPE_HARDWARE_MONITORING = "HARDWARE_MONITORING";
            public static final String TYPE_RESET_SERVER_PROGRESS = "RESET_SERVER_PROGRESS";
        }
    }
}
