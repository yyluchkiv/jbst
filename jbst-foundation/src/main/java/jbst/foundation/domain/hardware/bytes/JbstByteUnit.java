package jbst.foundation.domain.hardware.bytes;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum JbstByteUnit {
    KILOBYTE("KB"),
    MEGABYTE("MB"),
    GIGABYTE("GB");

    private final String symbol;

    @JsonValue
    public String getSymbol() {
        return this.symbol;
    }
}
