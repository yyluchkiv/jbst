package jbst.foundation.domain.exceptions.tokens;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JbstAccessTokenNotFoundExceptionTest {

    @Test
    void testException() {
        // Act
        var actual = new JbstAccessTokenNotFoundException();

        // Assert
        assertThat(actual.getMessage()).isEqualTo("JWT access token not found");
    }
}
