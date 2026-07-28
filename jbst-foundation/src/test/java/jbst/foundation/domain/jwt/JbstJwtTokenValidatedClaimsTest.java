package jbst.foundation.domain.jwt;

import jbst.foundation.domain.base.Username;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static jbst.foundation.domain.random.JbstRandom.validClaims;
import static jbst.foundation.domain.time.JbstTime.getCurrentTimestamp;
import static org.assertj.core.api.Assertions.assertThat;

class JbstJwtTokenValidatedClaimsTest {
    private static final Username INVALID = Username.of("invalid");

    @Test
    void invalidAccessTokenTest() {
        // Arrange
        var token = JbstJwtAccessToken.random();

        // Act
        var validatedClaims = JbstJwtTokenValidatedClaims.invalid(token);

        // Assert
        assertThat(validatedClaims.valid()).isFalse();
        assertThat(validatedClaims.isInvalid()).isTrue();
        assertThat(validatedClaims.isExpired()).isTrue();
        assertThat(validatedClaims.isAccess()).isTrue();
        assertThat(validatedClaims.isRefresh()).isFalse();
        assertThat(validatedClaims.jwtToken()).isEqualTo(token.value());
        assertThat(validatedClaims.username()).isEqualTo(INVALID);
        assertThat(validatedClaims.issuedAt().getTime()).isZero();
        assertThat(validatedClaims.getExpirationTimestamp()).isZero();
        assertThat(validatedClaims.authorities()).isEmpty();
    }

    @Test
    void invalidRefreshTokenTest() {
        // Arrange
        var token = JbstJwtRefreshToken.random();

        // Act
        var validatedClaims = JbstJwtTokenValidatedClaims.invalid(token);

        // Assert
        assertThat(validatedClaims.valid()).isFalse();
        assertThat(validatedClaims.isInvalid()).isTrue();
        assertThat(validatedClaims.isExpired()).isTrue();
        assertThat(validatedClaims.isAccess()).isFalse();
        assertThat(validatedClaims.isRefresh()).isTrue();
        assertThat(validatedClaims.jwtToken()).isEqualTo(token.value());
        assertThat(validatedClaims.username()).isEqualTo(INVALID);
        assertThat(validatedClaims.issuedAt().getTime()).isZero();
        assertThat(validatedClaims.getExpirationTimestamp()).isZero();
        assertThat(validatedClaims.authorities()).isEmpty();
    }

    @RepeatedTest(10)
    void validAccessTokenTest() {
        // Arrange
        var token = JbstJwtAccessToken.random();

        // Act
        var validatedClaims = JbstJwtTokenValidatedClaims.valid(token, validClaims());

        // Assert
        assertThat(validatedClaims.valid()).isTrue();
        assertThat(validatedClaims.isInvalid()).isFalse();
        assertThat(validatedClaims.isExpired()).isFalse();
        assertThat(validatedClaims.isAccess()).isTrue();
        assertThat(validatedClaims.isRefresh()).isFalse();
        assertThat(validatedClaims.jwtToken()).isEqualTo(token.value());
        assertThat(validatedClaims.username()).isEqualTo(Username.fixed());
        assertThat(validatedClaims.issuedAt().getTime()).isLessThanOrEqualTo(getCurrentTimestamp());
        // 3600000L == 1 hour
        assertThat(validatedClaims.getExpirationTimestamp() - validatedClaims.issuedAt().getTime()).isEqualTo(3600000L);
        assertThat(validatedClaims.authoritiesAsStrings()).isEqualTo(Set.of("admin", "user"));
    }

    @RepeatedTest(10)
    void validRefreshTokenTest() {
        // Arrange
        var token = JbstJwtRefreshToken.random();

        // Act
        var validatedClaims = JbstJwtTokenValidatedClaims.valid(token, validClaims());

        // Assert
        assertThat(validatedClaims.valid()).isTrue();
        assertThat(validatedClaims.isInvalid()).isFalse();
        assertThat(validatedClaims.isExpired()).isFalse();
        assertThat(validatedClaims.isAccess()).isFalse();
        assertThat(validatedClaims.isRefresh()).isTrue();
        assertThat(validatedClaims.jwtToken()).isEqualTo(token.value());
        assertThat(validatedClaims.username()).isEqualTo(Username.fixed());
        assertThat(validatedClaims.issuedAt().getTime()).isLessThanOrEqualTo(getCurrentTimestamp());
        // 3600000L == 1 hour
        assertThat(validatedClaims.getExpirationTimestamp() - validatedClaims.issuedAt().getTime()).isEqualTo(3600000L);
        assertThat(validatedClaims.authoritiesAsStrings()).isEqualTo(Set.of("admin", "user"));
    }
}
