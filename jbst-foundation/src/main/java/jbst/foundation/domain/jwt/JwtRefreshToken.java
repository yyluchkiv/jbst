package jbst.foundation.domain.jwt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jbst.foundation.domain.constants.JbstConstants;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jbst.foundation.utilities.random.RandomUtility.randomString;

public record JwtRefreshToken(@NotNull String value) {

    @JsonCreator
    public static JwtRefreshToken of(String value) {
        return new JwtRefreshToken(value);
    }

    public static JwtRefreshToken hardcoded() {
        return of("B7C50972C873270CD7B2");
    }

    public static JwtRefreshToken random() {
        return new JwtRefreshToken(randomString());
    }

    @SuppressWarnings("unused")
    public static JwtRefreshToken unknown() {
        return of(JbstConstants.Strings.UNKNOWN);
    }

    @SuppressWarnings("unused")
    public static Set<JwtRefreshToken> refreshTokens(String... tokens) {
        return Stream.of(tokens).map(JwtRefreshToken::new).collect(Collectors.toSet());
    }

    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}
