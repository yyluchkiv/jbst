package jbst.foundation.domain.exceptions.authentication;

import org.junit.jupiter.api.Test;

import static jbst.foundation.utilities.random.RandomUtility.randomString;
import static org.assertj.core.api.Assertions.assertThat;

class JbstRegistrationExceptionTest {

    @Test
    void testException() {
        // Arrange
        var message = randomString();

        // Act
        var actual = new JbstRegistrationException(message);

        // Assert
        assertThat(actual.getMessage()).isEqualTo(message);
    }
}
