package jbst.foundation.domain.exceptions.tokens;

import jbst.foundation.domain.base.Username;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JbstAccessTokenExpiredExceptionTest {

    @Test
    void testException() {
        // Arrange
        var username = Username.random();

        // Act
        var actual = new JbstAccessTokenExpiredException(username);

        // Assert
        assertThat(actual.getMessage()).isEqualTo("JWT access token is expired. Username: " + username);
    }
}
