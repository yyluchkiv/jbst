package jbst.foundation.domain.events;

import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringDatapointTableRow;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringDatapointTableView;
import jbst.foundation.domain.system.JbstSystemResetServerStatus;
import jbst.foundation.domain.tests.JbstUnitTests;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static jbst.foundation.domain.tests.JbstUnitTests.IO.read;
import static org.assertj.core.api.Assertions.assertThat;

class WebsocketEventTest extends JbstUnitTests.Runners.BaseFolder {

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @Test
    void serialize1Test() {
        // Arrange
        var websocketEvent = new WebsocketEvent();

        // Act
        var json = this.writeValueAsString(websocketEvent);

        // Assert
        assertThat(json).isEqualTo(read(this.getFolder(), "websocket-event-1.json"));
    }

    @Test
    void serialize2Test() {
        // Arrange
        var websocketEvent = new WebsocketEvent(
                Map.of(
                        "key2", 2L,
                        "key1", "value1",
                        "key3", true
                )
        );

        // Act
        var json = this.writeValueAsString(websocketEvent);

        // Assert
        assertThat(json).isEqualTo(read(this.getFolder(), "websocket-event-2.json"));
    }

    @Test
    void hardwareMonitoringTest() {
        var websocketEvent = WebsocketEvent.hardwareMonitoring(
                new HardwareMonitoringDatapointTableView(
                        List.of(
                                HardwareMonitoringDatapointTableRow.random(),
                                HardwareMonitoringDatapointTableRow.random()
                        )
                )
        );
        assertThat(websocketEvent.getAttributes())
                .hasSize(2)
                .containsKey("datapoint")
                .containsEntry("eventType", "HARDWARE_MONITORING");
        assertThat(websocketEvent.getAttributes().get("datapoint").getClass()).isEqualTo(HardwareMonitoringDatapointTableView.class);

        websocketEvent.add("key1", "value1");
        websocketEvent.add("key2", "value2");
        websocketEvent.add("key1", "value4");
        websocketEvent.add("key3", "value3");
        assertThat(websocketEvent.getAttributes()).hasSize(5);
    }

    @Test
    void resetServerProgressTest() {
        var websocketEvent = WebsocketEvent.resetServerProgress(
                new JbstSystemResetServerStatus(15)
        );
        assertThat(websocketEvent.getAttributes())
                .hasSize(2)
                .containsKey("status")
                .containsEntry("eventType", "RESET_SERVER_PROGRESS");
        assertThat(websocketEvent.getAttributes().get("status").getClass()).isEqualTo(JbstSystemResetServerStatus.class);

        websocketEvent.add("key1", "value1");
        websocketEvent.add("key2", "value2");
        websocketEvent.add("key1", "value4");
        websocketEvent.add("key3", "value3");
        assertThat(websocketEvent.getAttributes()).hasSize(5);
    }
}
