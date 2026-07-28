package jbst.foundation.domain.hardware.memories;

public record JbstSystemMemories(
        JbstGlobalMemory global,
        JbstCpuMemory cpu
) {

    public static JbstSystemMemories fixed() {
        return new JbstSystemMemories(
                JbstGlobalMemory.fixed(),
                JbstCpuMemory.fixed()
        );
    }
}
