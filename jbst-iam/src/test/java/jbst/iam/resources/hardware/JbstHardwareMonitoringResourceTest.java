package jbst.iam.resources.hardware;

import jbst.foundation.domain.base.Version;
import jbst.foundation.domain.hardware.memories.SystemMemories;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringDatapointTableView;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringMetadata;
import jbst.foundation.domain.hardware.monitoring.HardwareName;
import jbst.iam.configurations.TestRunnerResources1;
import jbst.iam.domain.db.JbstSettings;
import jbst.iam.settings.AbstractJbstSettingsService;
import jbst.iam.template.WssMessagingTemplate;
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
    private final AbstractJbstSettingsService jbstSettingsService;
    // Websockets
    private final WssMessagingTemplate wssMessagingTemplate;

    private final JbstHardwareMonitoringResource resourceUnderTest;

    @BeforeEach
    void beforeEach() {
        this.standaloneSetupByResourceUnderTest(this.resourceUnderTest);
        reset(
                this.jbstSettingsService,
                this.wssMessagingTemplate
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.jbstSettingsService,
                this.wssMessagingTemplate
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void saveMetadata() throws Exception {
        // Arrange
        var hardwareMonitoringMetadata = new HardwareMonitoringMetadata(
                Version.unknown(),
                SystemMemories.hardcoded()
        );
        when(this.jbstSettingsService.getSettings()).thenReturn(JbstSettings.hardcoded());

        // Act
        mvc.perform(
                post("/hardware/monitoring/metadata")
                        .content(this.getContent(hardwareMonitoringMetadata))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Assert
        var usernameAC = ArgumentCaptor.forClass(Set.class);
        var datapointTableViewAC = ArgumentCaptor.forClass(HardwareMonitoringDatapointTableView.class);
        verify(this.jbstSettingsService).getSettings();
        verify(this.wssMessagingTemplate).sendHardwareMonitoring(usernameAC.capture(), datapointTableViewAC.capture());
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
