package jbst.foundation.domain.http.requests;

import jbst.foundation.domain.tests.constants.TestsJunitConstants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static jbst.foundation.domain.random.JbstRandom.randomIPv4;
import static org.assertj.core.api.Assertions.assertThat;

class IPAddressTest {

    @Test
    void constructorsNullValueTest() {
        // Act
        var actual = new IPAddress(null);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.value()).isEqualTo("127.0.0.1");
    }

    @Test
    void constructorsValidValueTest() {
        // Arrange
        var value = randomIPv4();

        // Act
        var actual = new IPAddress(value);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.value()).isEqualTo(value);
    }

    @RepeatedTest(TestsJunitConstants.RANDOM_ITERATIONS_COUNT)
    void randomTest() {
        // Act
        var actual = IPAddress.random();

        // Assert
        var ipv4 = List.of(actual.value().split("\\."));
        assertThat(ipv4).hasSize(4);
        ipv4.forEach(element -> {
            var slot = Integer.valueOf(element);
            assertThat(slot).isNotNull();
            assertThat(slot).isNotNegative();
            assertThat(slot).isLessThan(256);
        });
    }
}
