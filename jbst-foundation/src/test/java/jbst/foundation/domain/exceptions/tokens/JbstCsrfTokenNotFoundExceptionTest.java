package jbst.foundation.domain.exceptions.tokens;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JbstCsrfTokenNotFoundExceptionTest {

    @Test
    void testException() {
        // Act
        var actual = new JbstCsrfTokenNotFoundException();

        // Assert
        assertThat(actual.getMessage()).isEqualTo("Csrf token not found");
    }
}
