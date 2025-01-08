package jbst.ops.server.domain.servers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.NotNull;

public record TeamV2(@NotNull String value) {
    @JsonCreator
    public static TeamV2 of(String value) {
        return new TeamV2(value);
    }

    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}
