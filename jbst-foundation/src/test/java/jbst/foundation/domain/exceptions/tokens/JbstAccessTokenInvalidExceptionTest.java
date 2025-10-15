package jbst.foundation.domain.exceptions.tokens;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JbstAccessTokenInvalidExceptionTest {

    @Test
    void testException() {
        // Act
        var actual = new JbstAccessTokenInvalidException();

        // Assert
        assertThat(actual.getMessage()).isEqualTo("JWT access token is invalid");
    }
}
