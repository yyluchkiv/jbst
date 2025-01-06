package jbst.ops.server.domain.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.NotNull;

import static jbst.foundation.utilities.random.RandomUtility.randomString;

public record AccessCode(@NotNull String value) {
    @JsonCreator
    public static AccessCode of(String value) {
        return new AccessCode(value);
    }

    public static AccessCode rnd() {
        return new AccessCode(randomString());
    }

    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}
