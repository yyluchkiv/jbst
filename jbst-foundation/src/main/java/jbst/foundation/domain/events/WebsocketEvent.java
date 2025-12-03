package jbst.foundation.domain.events;

import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringDatapointTableView;
import jbst.foundation.domain.system.JbstSystemResetServerStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class WebsocketEvent {
    private final Map<String, Object> attributes;

    public WebsocketEvent() {
        this.attributes = new HashMap<>();
    }

    public WebsocketEvent(Map<String, Object> attributes) {
        this.attributes = new HashMap<>(attributes);
    }

    public static WebsocketEvent hardwareMonitoring(@NotNull HardwareMonitoringDatapointTableView datapoint) {
        return new WebsocketEvent(
                Map.of(
                        WebsocketEventsAttributes.Keys.TYPE, WebsocketEventsAttributes.Values.TYPE_HARDWARE_MONITORING,
                        "datapoint", datapoint
                )
        );
    }

    public static WebsocketEvent resetServerProgress(@NotNull JbstSystemResetServerStatus status) {
        return new WebsocketEvent(
                Map.of(
                        WebsocketEventsAttributes.Keys.TYPE, WebsocketEventsAttributes.Values.TYPE_RESET_SERVER_PROGRESS,
                        "status", status
                )
        );
    }

    public void add(String key, Object value) {
        this.attributes.put(key, value);
    }
}
