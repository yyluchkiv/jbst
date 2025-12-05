package jbst.foundation.domain.hardware.memories;

public record JbstSystemMemories(
        JbstGlobalMemory global,
        JbstCpuMemory cpu
) {

    public static JbstSystemMemories hardcoded() {
        return new JbstSystemMemories(
                JbstGlobalMemory.hardcoded(),
                JbstCpuMemory.hardcoded()
        );
    }
}
