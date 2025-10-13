package jbst.foundation.domain.hardware.monitoring;

import jbst.foundation.domain.hardware.bytes.ByteSize;
import jbst.foundation.domain.hardware.memories.GlobalMemory;
import jbst.foundation.domain.hardware.memories.HeapMemory;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class HardwareMonitoringMaxValues {
    private final ByteSize server;
    private final ByteSize swap;
    private final ByteSize virtual;
    private final ByteSize heap;

    public HardwareMonitoringMaxValues(
            @NotNull GlobalMemory global,
            @NotNull HeapMemory heap
    ) {
        this.server = global.getTotal();
        this.swap = global.getSwapTotal();
        this.virtual = global.getVirtualTotal();
        this.heap = heap.getMax();
    }

    public static HardwareMonitoringMaxValues random() {
        return new HardwareMonitoringMaxValues(
                GlobalMemory.random(),
                HeapMemory.random()
        );
    }
}
