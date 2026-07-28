package jbst.foundation.domain.jwt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jbst.foundation.domain.constants.JbstConstants;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jbst.foundation.domain.random.JbstRandom.randomString;

public record JbstJwtAccessToken(@NotNull String value) {

    @JsonCreator
    public static JbstJwtAccessToken of(String value) {
        return new JbstJwtAccessToken(value);
    }

    public static JbstJwtAccessToken fixed() {
        return of("D9F4AF096BEE11C93D84");
    }

    public static JbstJwtAccessToken random() {
        return new JbstJwtAccessToken(randomString());
    }

    @SuppressWarnings("unused")
    public static JbstJwtAccessToken unknown() {
        return of(JbstConstants.Strings.UNKNOWN);
    }

    public static Set<JbstJwtAccessToken> accessTokens(String... tokens) {
        return Stream.of(tokens).map(JbstJwtAccessToken::new).collect(Collectors.toSet());
    }

    @SuppressWarnings("NullableProblems")
    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}
