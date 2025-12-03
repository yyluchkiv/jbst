package jbst.foundation.domain.exceptions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.NotNull;

public record JbstThrowableTrace(@NotNull String value) {

    @JsonCreator
    public static JbstThrowableTrace of(@NotNull String value) {
        return new JbstThrowableTrace(value);
    }

    @JsonValue
    @NotNull
    @Override
    public String toString() {
        return this.value;
    }
}
