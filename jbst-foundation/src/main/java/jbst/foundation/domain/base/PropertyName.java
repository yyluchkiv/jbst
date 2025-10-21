package jbst.foundation.domain.base;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.NotNull;

public record PropertyName(@NotNull String value) {
    @JsonCreator
    public static PropertyName of(String value) {
        return new PropertyName(value);
    }

    public static PropertyName hardcoded() {
        return of("32918411C6B8B4B1ABBE");
    }

    @SuppressWarnings("NullableProblems")
    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}
