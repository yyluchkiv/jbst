package jbst.foundation.domain.exceptions.tokens;

import jbst.foundation.domain.base.Username;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JbstRefreshTokenExpiredExceptionTest {

    @Test
    void testException() {
        // Arrange
        var username = Username.random();

        // Act
        var actual = new JbstRefreshTokenExpiredException(username);

        // Assert
        assertThat(actual.getMessage()).isEqualTo("JWT refresh token is expired. Username: " + username);
    }
}
