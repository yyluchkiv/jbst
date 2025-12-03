package jbst.foundation.domain.http;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.domain.exceptions.cookies.JbstCookieNotFoundException;
import jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static jbst.foundation.domain.http.JbstHttpCookies.*;
import static jbst.foundation.utilities.random.RandomUtility.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
class JbstHttpCookiesTest {

    private static Stream<Arguments> createCookieTests() {
        return Stream.of(
                Arguments.of(randomString(), randomString(), randomString(), randomBoolean(), 2),
                Arguments.of(randomString(), randomString(), randomString(), randomBoolean(), 200),
                Arguments.of(randomString(), randomString(), randomString(), randomBoolean(), Integer.MAX_VALUE),
                Arguments.of(randomString(), randomString(), randomString(), randomBoolean(), Integer.MAX_VALUE - 1)
        );
    }

    @ParameterizedTest
    @MethodSource("createCookieTests")
    void createCookieTest(String cookieKey, String cookieValue, String domain, boolean httpOnly, int maxAge) {
        // Act
        var actual = createCookie(cookieKey, cookieValue, domain, httpOnly, maxAge);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getPath()).isEqualTo("/");
        assertThat(actual.getName()).isEqualTo(cookieKey);
        assertThat(actual.getValue()).isEqualTo(cookieValue);
        assertThat(actual.getDomain()).isEqualTo(domain);
        assertThat(actual.isHttpOnly()).isEqualTo(httpOnly);
        assertThat(actual.getMaxAge()).isEqualTo(maxAge);
    }

    @ParameterizedTest
    @MethodSource("createCookieTests")
    void createNullCookieTest(String cookieKey, String cookieValue, String domain, boolean httpOnly, int maxAge) {
        // Act
        var actual = createNullCookie(cookieKey, domain);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getPath()).isEqualTo("/");
        assertThat(actual.getName()).isEqualTo(cookieKey);
        assertThat(actual.getValue()).isNull();
        assertThat(actual.getDomain()).isEqualTo(domain);
        assertThat(actual.isHttpOnly()).isTrue();
        assertThat(actual.getMaxAge()).isZero();
        // ignored
        assertThat(cookieValue).isNotNull();
        LOGGER.info("httpOnly is ignored: " + httpOnly);
        LOGGER.info("maxAge is ignored: " + maxAge);
    }

    @Test
    void readCookieExceptionTest() {
        // Arrange
        var request = mock(HttpServletRequest.class);
        var cookieKey = randomString();

        // Act
        var throwable = catchThrowable(() -> readCookie(request, cookieKey));

        // Assert
        assertThat(throwable)
                .isInstanceOf(JbstCookieNotFoundException.class)
                .hasMessage(ExceptionsMessagesUtility.entityNotFound("Cookie", cookieKey));
    }

    @Test
    void readCookieNoCookieTest() {
        // Arrange
        var cookie1 = createNullCookie("cookie1", randomString());
        var cookie2 = createNullCookie("cookie2", randomString());
        var cookies = new Cookie[] { cookie1, cookie2 };
        var request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(cookies);
        var cookieKey = "cookie3";

        // Act
        var throwable = catchThrowable(() -> readCookie(request, cookieKey));

        // Assert
        assertThat(throwable)
                .isInstanceOf(JbstCookieNotFoundException.class)
                .hasMessage(ExceptionsMessagesUtility.entityNotFound("Cookie", cookieKey));
    }

    @RepeatedTest(5)
    void readCookieTest() throws JbstCookieNotFoundException {
        // Arrange
        var cookieKey = randomString();
        var expected = randomString();
        var cookie1 = createCookie(cookieKey, expected, randomString(), randomBoolean(), randomIntegerGreaterThanZero());
        var cookie2 = createNullCookie(randomString(), randomString());
        var cookie3 = createNullCookie(randomString(), randomString());
        var cookies = new Cookie[] { cookie1, cookie2, cookie3 };
        var request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(cookies);

        // Act
        var actual = readCookie(request, cookieKey);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
