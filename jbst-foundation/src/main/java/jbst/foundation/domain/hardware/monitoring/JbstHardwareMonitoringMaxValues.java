package jbst.foundation.domain.hardware.monitoring;

import jbst.foundation.domain.hardware.bytes.JbstByteSize;
import jbst.foundation.domain.hardware.memories.JbstGlobalMemory;
import jbst.foundation.domain.hardware.memories.JbstHeapMemory;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class JbstHardwareMonitoringMaxValues {
    private final JbstByteSize server;
    private final JbstByteSize swap;
    private final JbstByteSize virtual;
    private final JbstByteSize heap;

    public JbstHardwareMonitoringMaxValues(
            @NotNull JbstGlobalMemory global,
            @NotNull JbstHeapMemory heap
    ) {
        this.server = global.getTotal();
        this.swap = global.getSwapTotal();
        this.virtual = global.getVirtualTotal();
        this.heap = heap.getMax();
    }

    public static JbstHardwareMonitoringMaxValues random() {
        return new JbstHardwareMonitoringMaxValues(
                JbstGlobalMemory.random(),
                JbstHeapMemory.random()
        );
    }
}
