package jbst.iam.domain.db;

import jbst.foundation.domain.base.Email;
import jbst.iam.domain.enums.UserTokenType;
import jbst.iam.domain.identifiers.TokenId;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static jbst.foundation.utilities.time.TimestampUtility.getCurrentTimestamp;
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

    @ParameterizedTest
    @MethodSource("isExpiredCases")
    void isExpiredTest(long expiryTimestamp, boolean expected) {
        // Arrange
        var userToken = new UserToken(
                TokenId.hardcoded(),
                Email.hardcoded(),
                "test-token-value",
                UserTokenType.MAGIC_LINK,
                expiryTimestamp,
                false
        );

        // Act + Assert
        assertThat(userToken.isExpired()).isEqualTo(expected);
    }
}
