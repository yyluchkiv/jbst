package jbst.foundation.resources.hardware;

import jbst.foundation.domain.base.Version;
import jbst.foundation.domain.databases.JbstSettings;
import jbst.foundation.domain.hardware.memories.CpuMemory;
import jbst.foundation.domain.hardware.memories.GlobalMemory;
import jbst.foundation.domain.hardware.memories.HeapMemory;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringDatapoint;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringDatapointTableRow;
import jbst.foundation.domain.hardware.monitoring.HardwareName;
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
        assertThat(widget1.datapoint()).isEqualTo(HardwareMonitoringDatapoint.zeroUsage().tableView(
                JbstSettings.hardcoded().hardwareMonitoringThresholds().values()
        ));

        // [1]
        var datapoint1 = HardwareMonitoringDatapoint.random();
        this.componentUnderTest.storeDatapoint(datapoint1);
        var containsOneElement2 = this.componentUnderTest.containsOneElement();
        assertThat(containsOneElement2).isTrue();

        // [2]
        var datapoint2 = HardwareMonitoringDatapoint.random();
        this.componentUnderTest.storeDatapoint(datapoint2);
        var containsOneElement3 = this.componentUnderTest.containsOneElement();
        assertThat(containsOneElement3).isFalse();

        // [3]
        var datapoint3 = new HardwareMonitoringDatapoint(
                Version.of("jbst vTEST"),
                GlobalMemory.hardcoded(),
                CpuMemory.hardcoded(),
                HeapMemory.hardcoded()
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
                        HardwareMonitoringDatapointTableRow::getHardwareName,
                        entry -> entry
                ));
        assertThat(mappedRows).hasSize(5);
        assertThat(mappedRows.get(HardwareName.CPU).getUsage()).isEqualTo(new BigDecimal("1.23"));
        assertThat(mappedRows.get(HardwareName.CPU).getValue()).isEmpty();
        assertThat(mappedRows.get(HardwareName.HEAP).getUsage()).isEqualTo(new BigDecimal("53.4"));
        assertThat(mappedRows.get(HardwareName.HEAP).getValue()).isEqualTo("0.53 GB of 1.00 GB");
        assertThat(mappedRows.get(HardwareName.SERVER).getUsage()).isEqualTo(new BigDecimal("45.6"));
        assertThat(mappedRows.get(HardwareName.SERVER).getValue()).isEqualTo("0.84 GB of 1.84 GB");
        assertThat(mappedRows.get(HardwareName.SWAP).getUsage()).isEqualTo(new BigDecimal("60.5"));
        assertThat(mappedRows.get(HardwareName.SWAP).getValue()).isEqualTo("1.00 GB of 1.65 GB");
        assertThat(mappedRows.get(HardwareName.VIRTUAL).getUsage()).isEqualTo(new BigDecimal("64.2"));
        assertThat(mappedRows.get(HardwareName.VIRTUAL).getValue()).isEqualTo("1.00 GB of 1.56 GB");
    }
}
