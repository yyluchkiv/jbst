package jbst.foundation.domain.hardware.monitoring;

import jbst.foundation.domain.base.Version;
import jbst.foundation.domain.hardware.memories.JbstSystemMemories;

public record JbstHardwareMonitoringMetadata(
        Version version,
        JbstSystemMemories systemMemories
) {

    public static JbstHardwareMonitoringMetadata fixed() {
        return new JbstHardwareMonitoringMetadata(
                Version.unknown(),
                JbstSystemMemories.fixed()
        );
    }
}
