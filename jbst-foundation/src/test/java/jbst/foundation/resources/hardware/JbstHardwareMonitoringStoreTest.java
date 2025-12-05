package jbst.foundation.resources.hardware;

import jbst.foundation.domain.base.Version;
import jbst.foundation.domain.databases.JbstSettings;
import jbst.foundation.domain.hardware.memories.JbstCpuMemory;
import jbst.foundation.domain.hardware.memories.JbstGlobalMemory;
import jbst.foundation.domain.hardware.memories.JbstHeapMemory;
import jbst.foundation.domain.hardware.monitoring.JbstHardwareMonitoringDatapoint;
import jbst.foundation.domain.hardware.monitoring.JbstHardwareMonitoringDatapointTableRow;
import jbst.foundation.domain.hardware.monitoring.JbstHardwareName;
import jbst.foundation.domain.settings.JbstSettingsHardwareMonitoringThresholds;
import jbst.foundation.settings.JbstSettingsService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstHardwareMonitoringStoreTest {

    @Configuration
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {

        @Bean
        JbstSettingsService jbstSettingsService() {
            var jbstSettingsService = mock(JbstSettingsService.class);
            when(jbstSettingsService.getHardwareMonitoringThresholds()).thenReturn(JbstSettingsHardwareMonitoringThresholds.hardcoded());
            return jbstSettingsService;
        }

        @Bean
        JbstHardwareMonitoringStore hardwareMonitoringStore() {
            return new JbstHardwareMonitoringStore(
                    this.jbstSettingsService()
            );
        }
    }

    private final JbstHardwareMonitoringStore componentUnderTest;

    @Test
    void integrationTest() {
        // [0]
        var containsOneElement1 = this.componentUnderTest.containsOneElement();
        var widget1 = this.componentUnderTest.getWidget();

        assertThat(containsOneElement1).isFalse();
        assertThat(widget1.version()).isEqualTo(Version.unknown());
        assertThat(widget1.datapoint()).isEqualTo(JbstHardwareMonitoringDatapoint.zeroUsage().tableView(
                JbstSettings.hardcoded().hardwareMonitoringThresholds().values()
        ));

        // [1]
        var datapoint1 = JbstHardwareMonitoringDatapoint.random();
        this.componentUnderTest.storeDatapoint(datapoint1);
        var containsOneElement2 = this.componentUnderTest.containsOneElement();
        assertThat(containsOneElement2).isTrue();

        // [2]
        var datapoint2 = JbstHardwareMonitoringDatapoint.random();
        this.componentUnderTest.storeDatapoint(datapoint2);
        var containsOneElement3 = this.componentUnderTest.containsOneElement();
        assertThat(containsOneElement3).isFalse();

        // [3]
        var datapoint3 = new JbstHardwareMonitoringDatapoint(
                Version.of("jbst vTEST"),
                JbstGlobalMemory.hardcoded(),
                JbstCpuMemory.hardcoded(),
                JbstHeapMemory.hardcoded()
        );
        this.componentUnderTest.storeDatapoint(datapoint3);
        var containsOneElement4 = this.componentUnderTest.containsOneElement();
        assertThat(containsOneElement4).isFalse();

        var widget2 = this.componentUnderTest.getWidget();

        assertThat(widget2.version().value()).isEqualTo("jbst vTEST");
        assertThat(widget2.datapoint().isAnyProblem()).isFalse();
        assertThat(widget2.datapoint().isAnyPresent()).isTrue();
        var mappedRows = widget2.datapoint().getRows().stream()
                .collect(Collectors.toMap(
                        JbstHardwareMonitoringDatapointTableRow::getHardwareName,
                        entry -> entry
                ));
        assertThat(mappedRows).hasSize(5);
        assertThat(mappedRows.get(JbstHardwareName.CPU).getUsage()).isEqualTo(new BigDecimal("1.23"));
        assertThat(mappedRows.get(JbstHardwareName.CPU).getValue()).isEmpty();
        assertThat(mappedRows.get(JbstHardwareName.HEAP).getUsage()).isEqualTo(new BigDecimal("53.4"));
        assertThat(mappedRows.get(JbstHardwareName.HEAP).getValue()).isEqualTo("0.53 GB of 1.00 GB");
        assertThat(mappedRows.get(JbstHardwareName.SERVER).getUsage()).isEqualTo(new BigDecimal("45.6"));
        assertThat(mappedRows.get(JbstHardwareName.SERVER).getValue()).isEqualTo("0.84 GB of 1.84 GB");
        assertThat(mappedRows.get(JbstHardwareName.SWAP).getUsage()).isEqualTo(new BigDecimal("60.5"));
        assertThat(mappedRows.get(JbstHardwareName.SWAP).getValue()).isEqualTo("1.00 GB of 1.65 GB");
        assertThat(mappedRows.get(JbstHardwareName.VIRTUAL).getUsage()).isEqualTo(new BigDecimal("64.2"));
        assertThat(mappedRows.get(JbstHardwareName.VIRTUAL).getValue()).isEqualTo("1.00 GB of 1.56 GB");
    }
}
