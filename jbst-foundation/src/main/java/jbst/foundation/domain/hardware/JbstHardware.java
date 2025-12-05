package jbst.foundation.domain.hardware;

import jbst.foundation.domain.hardware.memories.JbstCpuMemory;
import jbst.foundation.domain.hardware.memories.JbstGlobalMemory;
import jbst.foundation.domain.hardware.memories.JbstHeapMemory;
import jbst.foundation.domain.hardware.memories.JbstSystemMemories;
import lombok.experimental.UtilityClass;
import oshi.SystemInfo;
import oshi.util.Util;

import java.math.BigDecimal;

import static java.lang.management.ManagementFactory.getMemoryMXBean;

@UtilityClass
public class JbstHardware {

    public static JbstHeapMemory getHeapMemory() {
        var heapMemoryUsage = getMemoryMXBean().getHeapMemoryUsage();
        return new JbstHeapMemory(
                heapMemoryUsage.getInit(),
                heapMemoryUsage.getUsed(),
                heapMemoryUsage.getMax(),
                heapMemoryUsage.getCommitted()
        );
    }

    public static JbstSystemMemories getSystemMemories() {
        var systemInfo = new SystemInfo();
        var hardware = systemInfo.getHardware();
        // Hardware: Global
        var globalMemory = hardware.getMemory();
        var virtualMemory = globalMemory.getVirtualMemory();
        // Hardware: CPU
        var processor = hardware.getProcessor();
        var prevTicks = processor.getSystemCpuLoadTicks();
        Util.sleep(1000);
        // Hardware: System Memories
        // System CPU ticks is in range [0, 1]
        var systemCpuLoadBetweenTicks = processor.getSystemCpuLoadBetweenTicks(prevTicks);
        return new JbstSystemMemories(
                new JbstGlobalMemory(
                        globalMemory.getAvailable(),
                        globalMemory.getTotal(),
                        virtualMemory.getSwapUsed(),
                        virtualMemory.getSwapTotal(),
                        virtualMemory.getVirtualInUse(),
                        virtualMemory.getVirtualMax()
                ),
                new JbstCpuMemory(
                        BigDecimal.valueOf(systemCpuLoadBetweenTicks * 100)
                )
        );
    }
}
