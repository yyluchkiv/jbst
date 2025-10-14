package jbst.foundation.domain.http.requests;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.utilities.random.RandomUtility;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserAgentHeaderTest {

    @Test
    void constructorsRequestNull() {
        // Act
        var actual = new UserAgentHeader(null);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isEmpty();
    }

    @Test
    void constructorsRequestNoHeader() {
        // Arrange
        var request = mock(HttpServletRequest.class);

        // Act
        var actual = new UserAgentHeader(request);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isEmpty();
    }

    @Test
    void constructorsRequestValid() {
        // Arrange
        var userAgentHeader = RandomUtility.randomString();
        var request = mock(HttpServletRequest.class);
        when(request.getHeader("User-Agent")).thenReturn(userAgentHeader);

        // Act
        var actual = new UserAgentHeader(request);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isEqualTo(userAgentHeader);
    }
}
