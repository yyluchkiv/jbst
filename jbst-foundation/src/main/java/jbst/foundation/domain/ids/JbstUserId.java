package jbst.foundation.domain.ids;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jbst.foundation.domain.constants.JbstConstants;
import org.jetbrains.annotations.NotNull;

import static jbst.foundation.domain.random.JbstRandom.randomString;

public record JbstUserId(@NotNull String value) {

    @JsonCreator
    public static JbstUserId of(String value) {
        return new JbstUserId(value);
    }

    public static JbstUserId hardcoded() {
        return of("72667893848372913475");
    }

    public static JbstUserId random() {
        return new JbstUserId(randomString());
    }

    @SuppressWarnings("unused")
    public static JbstUserId unknown() {
        return of(JbstConstants.Strings.UNKNOWN);
    }

    @SuppressWarnings("NullableProblems")
    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}
