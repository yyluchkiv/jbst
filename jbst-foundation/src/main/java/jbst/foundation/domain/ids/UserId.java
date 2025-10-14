package jbst.foundation.domain.ids;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jbst.foundation.domain.constants.JbstConstants;
import org.jetbrains.annotations.NotNull;

import static jbst.foundation.utilities.random.RandomUtility.randomString;

public record UserId(@NotNull String value) {

    @JsonCreator
    public static UserId of(String value) {
        return new UserId(value);
    }

    public static UserId hardcoded() {
        return of("72667893848372913475");
    }

    public static UserId random() {
        return new UserId(randomString());
    }

    @SuppressWarnings("unused")
    public static UserId unknown() {
        return of(JbstConstants.Strings.UNKNOWN);
    }

    @SuppressWarnings("NullableProblems")
    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}
