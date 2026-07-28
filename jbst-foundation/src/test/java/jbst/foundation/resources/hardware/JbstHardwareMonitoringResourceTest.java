package jbst.foundation.resources.hardware;

import jbst.foundation.configurations.TestRunnerResources1;
import jbst.foundation.domain.hardware.monitoring.JbstHardwareMonitoringDatapointTableView;
import jbst.foundation.domain.hardware.monitoring.JbstHardwareMonitoringMetadata;
import jbst.foundation.domain.hardware.monitoring.JbstHardwareName;
import jbst.foundation.domain.settings.JbstSettingsHardwareMonitoringThresholds;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.settings.JbstSettingsService;
import jbst.foundation.websockets.JbstWebsocketsService;
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
    private final JbstSettingsService settingsService;
    // Websockets
    private final JbstWebsocketsService websocketsService;
    // Incidents
    private final JbstIncidentsPublisher incidentsPublisher;
    // State
    private final JbstHardwareMonitoringStore jbstHardwareMonitoringStore;

    private final JbstHardwareMonitoringResource resourceUnderTest;

    @BeforeEach
    void beforeEach() {
        this.standaloneSetupByResourceUnderTest(this.resourceUnderTest);
        this.jbstHardwareMonitoringStore.clear();
        reset(
                this.settingsService,
                this.websocketsService,
                this.incidentsPublisher
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.settingsService,
                this.websocketsService,
                this.incidentsPublisher
        );
    }

    @Test
    void saveMetadataIncidentScenario() throws Exception {
        // Arrange
        var npe = new NullPointerException("jbst-settings-exception");
        when(this.settingsService.getHardwareMonitoringThresholds()).thenThrow(npe);

        // Act
        mvc.perform(
                        post("/hardware/monitoring/metadata")
                                .content(this.getContent(JbstHardwareMonitoringMetadata.fixed()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Assert
        verify(this.settingsService).getHardwareMonitoringThresholds();
        verify(this.incidentsPublisher).publishThrowable(npe);
    }

    @SuppressWarnings("unchecked")
    @Test
    void saveMetadata() throws Exception {
        // Arrange
        when(this.settingsService.getHardwareMonitoringThresholds()).thenReturn(JbstSettingsHardwareMonitoringThresholds.fixed());

        // Act
        mvc.perform(
                post("/hardware/monitoring/metadata")
                        .content(this.getContent(JbstHardwareMonitoringMetadata.fixed()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Assert
        var usernameAC = ArgumentCaptor.forClass(Set.class);
        var datapointTableViewAC = ArgumentCaptor.forClass(JbstHardwareMonitoringDatapointTableView.class);
        verify(this.settingsService).getHardwareMonitoringThresholds();
        verify(this.websocketsService).sendHardwareMonitoring(usernameAC.capture(), datapointTableViewAC.capture());
        var datapoint = datapointTableViewAC.getValue();
        assertThat(datapoint.isAnyPresent()).isTrue();
        assertThat(datapoint.isAnyProblem()).isFalse();
        assertThat(datapoint.getRows()).hasSize(5);
        assertThat(datapoint.getMappedRows().get(JbstHardwareName.HEAP)).isNotNull();
        assertThat(datapoint.getMappedRows().get(JbstHardwareName.CPU).getUsage()).isEqualTo(new BigDecimal("1.23"));
        assertThat(datapoint.getMappedRows().get(JbstHardwareName.SERVER).getUsage()).isEqualTo(new BigDecimal("45.6"));
        assertThat(datapoint.getMappedRows().get(JbstHardwareName.SWAP).getUsage()).isEqualTo(new BigDecimal("60.5"));
        assertThat(datapoint.getMappedRows().get(JbstHardwareName.VIRTUAL).getUsage()).isEqualTo(new BigDecimal("64.2"));
    }
}
