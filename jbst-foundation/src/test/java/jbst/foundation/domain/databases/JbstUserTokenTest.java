package jbst.foundation.domain.databases;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.enums.JbstUserTokenType;
import jbst.foundation.domain.ids.JbstTokenId;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static jbst.foundation.domain.enums.JbstUserTokenType.*;
import static jbst.foundation.domain.time.JbstTime.getCurrentTimestamp;
import static org.assertj.core.api.Assertions.assertThat;

class JbstUserTokenTest {

    private static final long ONE_HOUR_MS = 3600000;
    private static final long ONE_DAY_MS = 86400000;

    // offsets are applied to the clock at execution time, far from the now-boundary, to keep assertions deterministic
    private static Stream<Arguments> isExpiredArgs() {
        return Stream.of(
                Arguments.of(-ONE_DAY_MS, true),
                Arguments.of(-ONE_HOUR_MS, true),
                Arguments.of(-1L, true),
                Arguments.of(ONE_HOUR_MS, false),
                Arguments.of(ONE_DAY_MS, false)
        );
    }

    private static Stream<Arguments> isInvalidArgs() {
        return Stream.of(
                Arguments.of(MAGICLINK, MAGICLINK, false, ONE_HOUR_MS, false),
                Arguments.of(EMAIL_CONFIRMATION, MAGICLINK, false, ONE_HOUR_MS, true),
                Arguments.of(MAGICLINK, MAGICLINK, true, ONE_HOUR_MS, true),
                Arguments.of(MAGICLINK, MAGICLINK, false, -ONE_HOUR_MS, true),
                Arguments.of(PASSWORD_RESET, MAGICLINK, true, ONE_HOUR_MS, true),
                Arguments.of(EMAIL_CONFIRMATION, MAGICLINK, false, -ONE_HOUR_MS, true),
                Arguments.of(MAGICLINK, MAGICLINK, true, -ONE_HOUR_MS, true),
                Arguments.of(PASSWORD_RESET, MAGICLINK, true, -ONE_HOUR_MS, true),
                Arguments.of(PASSWORD_RESET, EMAIL_CONFIRMATION, false, ONE_HOUR_MS, true),
                Arguments.of(EMAIL_CONFIRMATION, EMAIL_CONFIRMATION, false, ONE_HOUR_MS, false),
                Arguments.of(PASSWORD_RESET, PASSWORD_RESET, false, ONE_HOUR_MS, false)
        );
    }

    @ParameterizedTest
    @MethodSource("isExpiredArgs")
    void isExpiredTest(long expiryOffsetMs, boolean expected) {
        // Arrange
        var userToken = new JbstUserToken(
                JbstTokenId.fixed(),
                Email.fixed(),
                "test-token-value",
                MAGICLINK,
                getCurrentTimestamp() + expiryOffsetMs,
                false
        );

        // Act + Assert
        assertThat(userToken.isExpired()).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isInvalidArgs")
    void isInvalidTest(JbstUserTokenType tokenType, JbstUserTokenType expectedType, boolean used, long expiryOffsetMs, boolean expected) {
        // Arrange
        var userToken = new JbstUserToken(
                JbstTokenId.fixed(),
                Email.fixed(),
                "test-token-value",
                tokenType,
                getCurrentTimestamp() + expiryOffsetMs,
                used
        );

        // Act + Assert
        assertThat(userToken.isInvalid(expectedType)).isEqualTo(expected);
    }
}
