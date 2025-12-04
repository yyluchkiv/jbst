package jbst.foundation.domain.http.requests;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.domain.random.JbstRandom;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JbstUserAgentHeaderTest {

    @Test
    void constructorsRequestNull() {
        // Act
        var actual = new JbstUserAgentHeader(null);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isEmpty();
    }

    @Test
    void constructorsRequestNoHeader() {
        // Arrange
        var request = mock(HttpServletRequest.class);

        // Act
        var actual = new JbstUserAgentHeader(request);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isEmpty();
    }

    @Test
    void constructorsRequestValid() {
        // Arrange
        var userAgentHeader = JbstRandom.randomString();
        var request = mock(HttpServletRequest.class);
        when(request.getHeader("User-Agent")).thenReturn(userAgentHeader);

        // Act
        var actual = new JbstUserAgentHeader(request);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isEqualTo(userAgentHeader);
    }
}
