package jbst.foundation.domain.exceptions.geo;

import org.junit.jupiter.api.Test;

import static jbst.foundation.domain.random.JbstRandom.randomString;
import static org.assertj.core.api.Assertions.assertThat;

class JbstGeoLocationNotFoundExceptionTest {

    @Test
    void testException() {
        // Arrange
        var message = randomString();

        // Act
        var actual = new JbstGeoLocationNotFoundException(message);

        // Assert
        assertThat(actual.getMessage()).isEqualTo("Geo location not found: " + message);
    }
}
