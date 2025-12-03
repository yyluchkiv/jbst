package jbst.foundation.domain.exceptions.cookies;

import org.junit.jupiter.api.Test;

import static jbst.foundation.domain.random.JbstRandom.randomString;
import static org.assertj.core.api.Assertions.assertThat;

class JbstCookieNotFoundExceptionTest {

    @Test
    void testException() {
        // Arrange
        var message = randomString();

        // Act
        var actual = new JbstCookieNotFoundException(message);

        // Assert
        assertThat(actual.getMessage()).isEqualTo(message);
    }
}
