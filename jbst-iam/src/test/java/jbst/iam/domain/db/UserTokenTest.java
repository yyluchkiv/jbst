package jbst.iam.domain.db;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.enums.UserTokenType;
import jbst.foundation.domain.ids.TokenId;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static jbst.foundation.utilities.time.TimestampUtility.getCurrentTimestamp;
import static jbst.foundation.domain.enums.UserTokenType.*;
import static org.assertj.core.api.Assertions.assertThat;

class UserTokenTest {

    private static Stream<Arguments> isExpiredCases() {
        var currentTimestamp = getCurrentTimestamp();
        var pastTimestamp = currentTimestamp - 1000; // 1 second in the past
        var futureTimestamp = currentTimestamp + 1000; // 1 second in the future
        return Stream.of(
                Arguments.of(pastTimestamp, true),
                Arguments.of(futureTimestamp, false),
                Arguments.of(currentTimestamp, true),
                Arguments.of(currentTimestamp - 86400000, true),
                Arguments.of(currentTimestamp + 86400000, false)
        );
    }

    private static Stream<Arguments> isInvalidCases() {
        var currentTimestamp = getCurrentTimestamp();
        var pastTimestamp = currentTimestamp - 1000; // 1 second in the past
        var futureTimestamp = currentTimestamp + 1000; // 1 second in the future

        return Stream.of(
                Arguments.of(MAGIC_LINK, MAGIC_LINK, false, futureTimestamp, false),
                Arguments.of(EMAIL_CONFIRMATION, MAGIC_LINK, false, futureTimestamp, true),
                Arguments.of(MAGIC_LINK, MAGIC_LINK, true, futureTimestamp, true),
                Arguments.of(MAGIC_LINK, MAGIC_LINK, false, pastTimestamp, true),
                Arguments.of(PASSWORD_RESET, MAGIC_LINK, true, futureTimestamp, true),
                Arguments.of(EMAIL_CONFIRMATION, MAGIC_LINK, false, pastTimestamp, true),
                Arguments.of(MAGIC_LINK, MAGIC_LINK, true, pastTimestamp, true),
                Arguments.of(PASSWORD_RESET, MAGIC_LINK, true, pastTimestamp, true),
                Arguments.of(PASSWORD_RESET, EMAIL_CONFIRMATION, false, futureTimestamp, true),
                Arguments.of(EMAIL_CONFIRMATION, EMAIL_CONFIRMATION, false, futureTimestamp, false),
                Arguments.of(PASSWORD_RESET, PASSWORD_RESET, false, futureTimestamp, false)
        );
    }

    @ParameterizedTest
    @MethodSource("isExpiredCases")
    void isExpiredTest(long expiryTimestamp, boolean expected) {
        // Arrange
        var userToken = new UserToken(
                TokenId.hardcoded(),
                Email.hardcoded(),
                "test-token-value",
                MAGIC_LINK,
                expiryTimestamp,
                false
        );

        // Act + Assert
        assertThat(userToken.isExpired()).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isInvalidCases")
    void isInvalidTest(UserTokenType tokenType, UserTokenType expectedType, boolean used, long expiryTimestamp, boolean expected) {
        // Arrange
        var userToken = new UserToken(
                TokenId.hardcoded(),
                Email.hardcoded(),
                "test-token-value",
                tokenType,
                expiryTimestamp,
                used
        );

        // Act + Assert
        assertThat(userToken.isInvalid(expectedType)).isEqualTo(expected);
    }
}
