package jbst.foundation.resources.hardware;

import jbst.foundation.configurations.TestRunnerResources1;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringDatapointTableView;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringMetadata;
import jbst.foundation.domain.hardware.monitoring.HardwareName;
import jbst.foundation.domain.settings.JbstSettingsHardwareMonitoringThresholds;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import jbst.foundation.settings.JbstSettingsService;
import jbst.foundation.websockets.WebsocketsService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstHardwareMonitoringResourceTest extends TestRunnerResources1 {

    // Settings
    private final JbstSettingsService jbstSettingsService;
    // Websockets
    private final WebsocketsService websocketsService;
    // Incidents
    private final IncidentPublisher incidentPublisher;
    // State
    private final JbstHardwareMonitoringStore jbstHardwareMonitoringStore;

    private final JbstHardwareMonitoringResource resourceUnderTest;

    @BeforeEach
    void beforeEach() {
        this.standaloneSetupByResourceUnderTest(this.resourceUnderTest);
        this.jbstHardwareMonitoringStore.clear();
        reset(
                this.jbstSettingsService,
                this.websocketsService,
                this.incidentPublisher
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.jbstSettingsService,
                this.websocketsService,
                this.incidentPublisher
        );
    }

    @Test
    void saveMetadataIncidentScenario() throws Exception {
        // Arrange
        var npe = new NullPointerException("jbst-settings-exception");
        when(this.jbstSettingsService.getHardwareMonitoringThresholds()).thenThrow(npe);

        // Act
        mvc.perform(
                        post("/hardware/monitoring/metadata")
                                .content(this.getContent(HardwareMonitoringMetadata.hardcoded()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Assert
        verify(this.jbstSettingsService).getHardwareMonitoringThresholds();
        verify(this.incidentPublisher).publishThrowable(npe);
    }

    @SuppressWarnings("unchecked")
    @Test
    void saveMetadata() throws Exception {
        // Arrange
        when(this.jbstSettingsService.getHardwareMonitoringThresholds()).thenReturn(JbstSettingsHardwareMonitoringThresholds.hardcoded());

        // Act
        mvc.perform(
                post("/hardware/monitoring/metadata")
                        .content(this.getContent(HardwareMonitoringMetadata.hardcoded()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Assert
        var usernameAC = ArgumentCaptor.forClass(Set.class);
        var datapointTableViewAC = ArgumentCaptor.forClass(HardwareMonitoringDatapointTableView.class);
        verify(this.jbstSettingsService).getHardwareMonitoringThresholds();
        verify(this.websocketsService).sendHardwareMonitoring(usernameAC.capture(), datapointTableViewAC.capture());
        var datapoint = datapointTableViewAC.getValue();
        assertThat(datapoint.isAnyPresent()).isTrue();
        assertThat(datapoint.isAnyProblem()).isFalse();
        assertThat(datapoint.getRows()).hasSize(5);
        assertThat(datapoint.getMappedRows().get(HardwareName.HEAP)).isNotNull();
        assertThat(datapoint.getMappedRows().get(HardwareName.CPU).getUsage()).isEqualTo(new BigDecimal("1.23"));
        assertThat(datapoint.getMappedRows().get(HardwareName.SERVER).getUsage()).isEqualTo(new BigDecimal("45.6"));
        assertThat(datapoint.getMappedRows().get(HardwareName.SWAP).getUsage()).isEqualTo(new BigDecimal("60.5"));
        assertThat(datapoint.getMappedRows().get(HardwareName.VIRTUAL).getUsage()).isEqualTo(new BigDecimal("64.2"));
    }
}
