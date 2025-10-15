package jbst.foundation.domain.exceptions.tokens;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JbstRefreshTokenInvalidExceptionTest {

    @Test
    void testException() {
        // Act
        var actual = new JbstRefreshTokenInvalidException();

        // Assert
        assertThat(actual.getMessage()).isEqualTo("JWT refresh token is invalid");
    }
}
