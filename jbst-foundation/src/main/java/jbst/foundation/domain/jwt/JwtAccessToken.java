package jbst.foundation.domain.jwt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jbst.foundation.domain.constants.JbstConstants;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jbst.foundation.utilities.random.RandomUtility.randomString;

public record JwtAccessToken(@NotNull String value) {

    @JsonCreator
    public static JwtAccessToken of(String value) {
        return new JwtAccessToken(value);
    }

    public static JwtAccessToken hardcoded() {
        return of("D9F4AF096BEE11C93D84");
    }

    public static JwtAccessToken random() {
        return new JwtAccessToken(randomString());
    }

    @SuppressWarnings("unused")
    public static JwtAccessToken unknown() {
        return of(JbstConstants.Strings.UNKNOWN);
    }

    public static Set<JwtAccessToken> accessTokens(String... tokens) {
        return Stream.of(tokens).map(JwtAccessToken::new).collect(Collectors.toSet());
    }

    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}
