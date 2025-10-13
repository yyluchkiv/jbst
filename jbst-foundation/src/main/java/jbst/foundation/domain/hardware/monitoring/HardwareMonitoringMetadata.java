package jbst.foundation.domain.hardware.monitoring;

import jbst.foundation.domain.base.Version;
import jbst.foundation.domain.hardware.memories.SystemMemories;

public record HardwareMonitoringMetadata(
        Version version,
        SystemMemories systemMemories
) {

    public static HardwareMonitoringMetadata hardcoded() {
        return new HardwareMonitoringMetadata(
                Version.unknown(),
                SystemMemories.hardcoded()
        );
    }
}
