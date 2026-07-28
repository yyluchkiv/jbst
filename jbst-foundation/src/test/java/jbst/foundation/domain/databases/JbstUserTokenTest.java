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

    private static Stream<Arguments> isExpiredArgs() {
        var currentTimestamp = getCurrentTimestamp();
        var pastTimestamp = currentTimestamp - 3000; // 2 seconds in the past
        var futureTimestamp = currentTimestamp + 3000; // 2 seconds in the future
        return Stream.of(
                Arguments.of(pastTimestamp, true),
                Arguments.of(futureTimestamp, false),
                Arguments.of(currentTimestamp, true),
                Arguments.of(currentTimestamp - 86400000, true),
                Arguments.of(currentTimestamp + 86400000, false)
        );
    }

    private static Stream<Arguments> isInvalidArgs() {
        var currentTimestamp = getCurrentTimestamp();
        var pastTimestamp = currentTimestamp - 1000; // 1 second in the past
        var futureTimestamp = currentTimestamp + 1000; // 1 second in the future

        return Stream.of(
                Arguments.of(MAGICLINK, MAGICLINK, false, futureTimestamp, false),
                Arguments.of(EMAIL_CONFIRMATION, MAGICLINK, false, futureTimestamp, true),
                Arguments.of(MAGICLINK, MAGICLINK, true, futureTimestamp, true),
                Arguments.of(MAGICLINK, MAGICLINK, false, pastTimestamp, true),
                Arguments.of(PASSWORD_RESET, MAGICLINK, true, futureTimestamp, true),
                Arguments.of(EMAIL_CONFIRMATION, MAGICLINK, false, pastTimestamp, true),
                Arguments.of(MAGICLINK, MAGICLINK, true, pastTimestamp, true),
                Arguments.of(PASSWORD_RESET, MAGICLINK, true, pastTimestamp, true),
                Arguments.of(PASSWORD_RESET, EMAIL_CONFIRMATION, false, futureTimestamp, true),
                Arguments.of(EMAIL_CONFIRMATION, EMAIL_CONFIRMATION, false, futureTimestamp, false),
                Arguments.of(PASSWORD_RESET, PASSWORD_RESET, false, futureTimestamp, false)
        );
    }

    @ParameterizedTest
    @MethodSource("isExpiredArgs")
    void isExpiredTest(long expiryTimestamp, boolean expected) {
        // Arrange
        var userToken = new JbstUserToken(
                JbstTokenId.fixed(),
                Email.fixed(),
                "test-token-value",
                MAGICLINK,
                expiryTimestamp,
                false
        );

        // Act + Assert
        assertThat(userToken.isExpired()).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isInvalidArgs")
    void isInvalidTest(JbstUserTokenType tokenType, JbstUserTokenType expectedType, boolean used, long expiryTimestamp, boolean expected) {
        // Arrange
        var userToken = new JbstUserToken(
                JbstTokenId.fixed(),
                Email.fixed(),
                "test-token-value",
                tokenType,
                expiryTimestamp,
                used
        );

        // Act + Assert
        assertThat(userToken.isInvalid(expectedType)).isEqualTo(expected);
    }
}
