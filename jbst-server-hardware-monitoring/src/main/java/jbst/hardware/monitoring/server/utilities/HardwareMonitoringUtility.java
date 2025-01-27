package jbst.hardware.monitoring.server.utilities;

import jbst.foundation.domain.hardware.memories.CpuMemory;
import jbst.foundation.domain.hardware.memories.GlobalMemory;
import jbst.foundation.domain.hardware.memories.SystemMemories;
import jbst.foundation.domain.tuples.Tuple2;
import lombok.experimental.UtilityClass;
import oshi.SystemInfo;
import oshi.util.Util;

import java.math.BigDecimal;

@UtilityClass
public class HardwareMonitoringUtility {

    public static SystemMemories getSystemMemories() {
        var memories = getMemories();
        return new SystemMemories(
                memories.a(),
                memories.b()
        );
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private static Tuple2<GlobalMemory, CpuMemory> getMemories() {
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
        return new Tuple2<>(
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
