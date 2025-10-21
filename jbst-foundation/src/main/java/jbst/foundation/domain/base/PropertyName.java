package jbst.foundation.domain.base;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jbst.foundation.domain.constants.JbstConstants;
import org.jetbrains.annotations.NotNull;

import static jbst.foundation.utilities.random.RandomUtility.randomString;

public record PropertyName(@NotNull String value) {
    @JsonCreator
    public static PropertyName of(String value) {
        return new PropertyName(value);
    }

    public static PropertyName hardcoded() {
        return of("32918411C6B8B4B1ABBE");
    }

    public static PropertyName random() {
        return of(randomString());
    }

    public static PropertyName undefined() {
        return of(JbstConstants.Strings.UNDEFINED);
    }

    public static PropertyName dash() {
        return of(JbstConstants.Symbols.DASH);
    }

    @SuppressWarnings("unused")
    public static PropertyName hyphen() {
        return of(JbstConstants.Symbols.HYPHEN);
    }

    @SuppressWarnings("NullableProblems")
    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}
