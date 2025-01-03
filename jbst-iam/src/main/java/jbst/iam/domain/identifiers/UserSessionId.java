package jbst.iam.domain.identifiers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jbst.foundation.domain.constants.JbstConstants;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record UserSessionId(@NotNull String value) {

    @JsonCreator
    public static UserSessionId of(String value) {
        return new UserSessionId(value);
    }

    public static UserSessionId undefined() {
        return new UserSessionId(JbstConstants.Strings.UNDEFINED);
    }

    public static UserSessionId random() {
        return new UserSessionId(UUID.randomUUID().toString());
    }

    @SuppressWarnings("unused")
    public static UserSessionId unknown() {
        return of(JbstConstants.Strings.UNKNOWN);
    }

    public static UserSessionId hardcoded() {
        return of("8DE052C55BD26A1A6F0E");
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
