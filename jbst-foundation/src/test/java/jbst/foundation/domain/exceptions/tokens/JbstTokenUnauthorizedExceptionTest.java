package jbst.foundation.domain.exceptions.tokens;

import org.junit.jupiter.api.Test;

import static jbst.foundation.utilities.random.RandomUtility.randomString;
import static org.assertj.core.api.Assertions.assertThat;

class JbstTokenUnauthorizedExceptionTest {

    @Test
    void testException() {
        // Arrange
        var message = randomString();

        // Act
        var actual = new JbstTokenUnauthorizedException(message);

        // Assert
        assertThat(actual.getMessage()).isEqualTo(message);
    }
}
