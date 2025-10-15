package jbst.foundation.domain.exceptions.tokens;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JbstRefreshTokenNotFoundExceptionTest {

    @Test
    void testException() {
        // Act
        var actual = new JbstRefreshTokenNotFoundException();

        // Assert
        assertThat(actual.getMessage()).isEqualTo("JWT refresh token not found");
    }
}
