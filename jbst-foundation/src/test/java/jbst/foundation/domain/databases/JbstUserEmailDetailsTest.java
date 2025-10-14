package jbst.foundation.domain.databases;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class JbstUserEmailDetailsTest {

    private static Stream<Arguments> isEnabledTest() {
        return Stream.of(
                Arguments.of(JbstUserEmailDetails.required(), false),
                Arguments.of(JbstUserEmailDetails.unnecessary(), true),
                Arguments.of(JbstUserEmailDetails.confirmed(), true)
        );
    }

    @ParameterizedTest
    @MethodSource("isEnabledTest")
    void isEnabledTest(JbstUserEmailDetails details, boolean expected) {
        // Act
        var actual = details.isEnabled();

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

}
