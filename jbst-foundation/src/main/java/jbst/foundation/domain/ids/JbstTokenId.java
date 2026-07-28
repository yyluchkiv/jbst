package jbst.foundation.domain.ids;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jbst.foundation.domain.constants.JbstConstants;
import org.jetbrains.annotations.NotNull;

import static jbst.foundation.domain.random.JbstRandom.randomString;

public record JbstTokenId(@NotNull String value) {

    @JsonCreator
    public static JbstTokenId of(String value) {
        return new JbstTokenId(value);
    }

    public static JbstTokenId fixed() {
        return of("75e0d0dfc0d34914a1c49305d6477abd");
    }

    public static JbstTokenId random() {
        return new JbstTokenId(randomString());
    }

    @SuppressWarnings("unused")
    public static JbstTokenId unknown() {
        return of(JbstConstants.Strings.UNKNOWN);
    }

    @SuppressWarnings("NullableProblems")
    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }

}
