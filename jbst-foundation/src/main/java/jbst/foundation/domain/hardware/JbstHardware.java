package jbst.foundation.domain.hardware;

import jbst.foundation.domain.hardware.memories.CpuMemory;
import jbst.foundation.domain.hardware.memories.GlobalMemory;
import jbst.foundation.domain.hardware.memories.HeapMemory;
import jbst.foundation.domain.hardware.memories.SystemMemories;
import lombok.experimental.UtilityClass;
import oshi.SystemInfo;
import oshi.util.Util;

import java.math.BigDecimal;

import static java.lang.management.ManagementFactory.getMemoryMXBean;

@UtilityClass
public class JbstHardware {

    public static HeapMemory getHeapMemory() {
        var heapMemoryUsage = getMemoryMXBean().getHeapMemoryUsage();
        return new HeapMemory(
                heapMemoryUsage.getInit(),
                heapMemoryUsage.getUsed(),
                heapMemoryUsage.getMax(),
                heapMemoryUsage.getCommitted()
        );
    }

    public static SystemMemories getSystemMemories() {
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
        return new SystemMemories(
                new GlobalMemory(
                        globalMemory.getAvailable(),
                        globalMemory.getTotal(),
                        virtualMemory.getSwapUsed(),
                        virtualMemory.getSwapTotal(),
                        virtualMemory.getVirtualInUse(),
                        virtualMemory.getVirtualMax()
                ),
                new CpuMemory(
                        BigDecimal.valueOf(systemCpuLoadBetweenTicks * 100)
                )
        );
    }
}
