package jbst.foundation.domain.exceptions.tokens;

import jbst.foundation.domain.base.Username;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JbstAccessTokenDbNotFoundExceptionTest {

    @Test
    void testException() {
        // Arrange
        var username = Username.random();

        // Act
        var actual = new JbstAccessTokenDbNotFoundException(username);

        // Assert
        assertThat(actual.getMessage()).isEqualTo("JWT access token is not present in database. Username: " + username);
    }
}
