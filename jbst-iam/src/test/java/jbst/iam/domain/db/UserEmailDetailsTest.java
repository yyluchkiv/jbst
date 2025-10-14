package jbst.iam.domain.db;

import jbst.foundation.domain.databases.UserEmailDetails;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class UserEmailDetailsTest {

    private static Stream<Arguments> isEnabledTest() {
        return Stream.of(
                Arguments.of(UserEmailDetails.required(), false),
                Arguments.of(UserEmailDetails.unnecessary(), true),
                Arguments.of(UserEmailDetails.confirmed(), true)
        );
    }

    @ParameterizedTest
    @MethodSource("isEnabledTest")
    void isEnabledTest(UserEmailDetails details, boolean expected) {
        // Act
        var actual = details.isEnabled();

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

}
