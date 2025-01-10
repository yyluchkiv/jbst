package jbst.ops.server.domain.servers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.NotNull;

public record Team(@NotNull String value) {
    @JsonCreator
    public static Team of(String value) {
        return new Team(value);
    }

    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}
