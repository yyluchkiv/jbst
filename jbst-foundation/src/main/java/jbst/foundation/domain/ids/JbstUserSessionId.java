package jbst.foundation.domain.ids;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jbst.foundation.domain.constants.JbstConstants;
import org.jetbrains.annotations.NotNull;

import static jbst.foundation.domain.random.JbstRandom.randomString;

public record JbstUserSessionId(@NotNull String value) {

    @JsonCreator
    public static JbstUserSessionId of(String value) {
        return new JbstUserSessionId(value);
    }

    public static JbstUserSessionId undefined() {
        return new JbstUserSessionId(JbstConstants.Strings.UNDEFINED);
    }

    public static JbstUserSessionId random() {
        return new JbstUserSessionId(randomString());
    }

    @SuppressWarnings("unused")
    public static JbstUserSessionId unknown() {
        return of(JbstConstants.Strings.UNKNOWN);
    }

    public static JbstUserSessionId fixed() {
        return of("8DE052C55BD26A1A6F0E");
    }

    @SuppressWarnings("NullableProblems")
    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}
