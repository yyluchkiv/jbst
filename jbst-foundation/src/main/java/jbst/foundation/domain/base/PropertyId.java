package jbst.foundation.domain.base;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.NotNull;

public record PropertyId(@NotNull String value) {
    @JsonCreator
    public static PropertyId of(String value) {
        return new PropertyId(value);
    }

    public static PropertyId hardcoded() {
        return of("A0814EF707DAF2FDE2D4");
    }

    @SuppressWarnings("NullableProblems")
    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}
