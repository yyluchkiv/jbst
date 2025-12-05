package jbst.foundation.domain.hardware.monitoring;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum JbstHardwareName {
    CPU("CPU"),
    HEAP("Heap"),
    SERVER("Server"),
    SWAP("Swap"),
    VIRTUAL("Virtual");

    private final String value;

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
