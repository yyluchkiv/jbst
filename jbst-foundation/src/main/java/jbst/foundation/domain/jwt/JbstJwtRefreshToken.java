package jbst.foundation.domain.jwt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jbst.foundation.domain.constants.JbstConstants;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jbst.foundation.domain.random.JbstRandom.randomString;

public record JbstJwtRefreshToken(@NotNull String value) {

    @JsonCreator
    public static JbstJwtRefreshToken of(String value) {
        return new JbstJwtRefreshToken(value);
    }

    public static JbstJwtRefreshToken fixed() {
        return of("B7C50972C873270CD7B2");
    }

    public static JbstJwtRefreshToken random() {
        return new JbstJwtRefreshToken(randomString());
    }

    @SuppressWarnings("unused")
    public static JbstJwtRefreshToken unknown() {
        return of(JbstConstants.Strings.UNKNOWN);
    }

    @SuppressWarnings("unused")
    public static Set<JbstJwtRefreshToken> refreshTokens(String... tokens) {
        return Stream.of(tokens).map(JbstJwtRefreshToken::new).collect(Collectors.toSet());
    }

    @SuppressWarnings("NullableProblems")
    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}
