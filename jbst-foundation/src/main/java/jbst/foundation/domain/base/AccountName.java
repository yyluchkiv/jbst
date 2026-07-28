package jbst.foundation.domain.base;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.NotNull;

import static jbst.foundation.domain.random.JbstRandom.randomStringLetterOrNumbersOnly;

public record AccountName(@NotNull String value) {
    @JsonCreator
    public static AccountName of(String value) {
        return new AccountName(value);
    }

    public static AccountName fixed() {
        return of("Account0");
    }

    public static AccountName random() {
        return new AccountName(randomStringLetterOrNumbersOnly(10));
    }

    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}
