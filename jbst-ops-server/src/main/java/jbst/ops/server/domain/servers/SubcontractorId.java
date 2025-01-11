package jbst.ops.server.domain.servers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.NotNull;

@Deprecated(forRemoval = true)
public record SubcontractorId(@NotNull String value) {
    @JsonCreator
    public static SubcontractorId of(String value) {
        return new SubcontractorId(value);
    }

    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}
