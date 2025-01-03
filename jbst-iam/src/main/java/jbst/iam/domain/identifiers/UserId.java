package jbst.iam.domain.identifiers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jbst.foundation.domain.constants.JbstConstants;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record UserId(@NotNull String value) {

    @JsonCreator
    public static UserId of(String value) {
        return new UserId(value);
    }

    public static UserId hardcoded() {
        return of("72667893848372913475");
    }

    public static UserId random() {
        return new UserId(UUID.randomUUID().toString());
    }

    @SuppressWarnings("unused")
    public static UserId unknown() {
        return of(JbstConstants.Strings.UNKNOWN);
    }

    public UUID asUUID() {
        return UUID.fromString(this.value);
    }

    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}
