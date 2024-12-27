package jbst.foundation.services.hardware.store.impl;

import jbst.foundation.configurations.TestJbstConfigurationPropertiesHardcoded;
import jbst.foundation.domain.base.Version;
import jbst.foundation.domain.events.hardware.EventLastHardwareMonitoringDatapoint;
import jbst.foundation.domain.hardware.memories.CpuMemory;
import jbst.foundation.domain.hardware.memories.GlobalMemory;
import jbst.foundation.domain.hardware.memories.HeapMemory;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringDatapoint;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringDatapointTableRow;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringThresholds;
import jbst.foundation.domain.hardware.monitoring.HardwareName;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.services.hardware.store.HardwareMonitoringStore;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class HardwareMonitoringStoreImplTest {

    @Configuration
    @Import({
            TestJbstConfigurationPropertiesHardcoded.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final JbstProperties jbstProperties;

        @Bean
        HardwareMonitoringStore hardwareMonitoringStore() {
            return new HardwareMonitoringStoreImpl(
                    this.jbstProperties
            );
        }
    }

    private final JbstProperties jbstProperties;

    private final HardwareMonitoringStore componentUnderTest;

    @Test
    void integrationTest() {
        // Arrange
        var thresholdsConfigs = new HardwareMonitoringThresholds(
                this.jbstProperties.getHardwareMonitoringConfigs().getThresholdsConfigs()
        );

        // [0]
        var containsOneElement1 = this.componentUnderTest.containsOneElement();
        var widget1 = this.componentUnderTest.getHardwareMonitoringWidget();

        assertThat(containsOneElement1).isFalse();
        assertThat(widget1.version()).isEqualTo(Version.unknown());
        assertThat(widget1.datapoint()).isEqualTo(HardwareMonitoringDatapoint.zeroUsage().tableView(thresholdsConfigs));

        // [1]
        var event1 = EventLastHardwareMonitoringDatapoint.random();
        this.componentUnderTest.storeEvent(event1);
        var containsOneElement2 = this.componentUnderTest.containsOneElement();
        assertThat(containsOneElement2).isTrue();

        // [2]
        var event2 = EventLastHardwareMonitoringDatapoint.random();
        this.componentUnderTest.storeEvent(event2);
        var containsOneElement3 = this.componentUnderTest.containsOneElement();
        assertThat(containsOneElement3).isFalse();

        // [3]
        var event3 = new EventLastHardwareMonitoringDatapoint(
                Version.of("jbst vTEST"),
                new HardwareMonitoringDatapoint(
                        GlobalMemory.hardcoded(),
                        CpuMemory.hardcoded(),
                        HeapMemory.hardcoded()
                )
        );
        this.componentUnderTest.storeEvent(event3);
        var containsOneElement4 = this.componentUnderTest.containsOneElement();
        assertThat(containsOneElement4).isFalse();

        var widget2 = this.componentUnderTest.getHardwareMonitoringWidget();

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
