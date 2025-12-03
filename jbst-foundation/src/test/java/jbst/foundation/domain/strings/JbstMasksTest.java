package jbst.foundation.domain.strings;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static jbst.foundation.utilities.random.RandomUtility.randomIntegerGreaterThanZeroByBounds;
import static jbst.foundation.domain.strings.JbstMasks.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

class JbstMasksTest {

    private static Stream<Arguments> maskTests() {
        return Stream.of(
                Arguments.of("AaBbCcDdEeFfGgHgIiJjKk", "AaBbC*****************", "AaBbCcDd**************", "Aa********************"),
                Arguments.of("123456789", "12345****", "12345678*", "12*******"),
                Arguments.of("12345678", "12345***", "12345678", "12******"),
                Arguments.of("123456", "12345*", "123456", "12****"),
                Arguments.of("12345", "12345", "12345", "12***"),
                Arguments.of("1234", "1234", "1234", "12**"),
                Arguments.of("1", "1", "1", "1")
        );
    }

    private static Stream<Arguments> cutMaskTests() {
        return Stream.of(
                Arguments.of("AaBbCcDdEeFfGgHgIiJjKk", "AaBbCcDd*******", "AaBbC*****"),
                Arguments.of("123456789", "12345678*******", "12345*****"),
                Arguments.of("12345", "12345**********", "12345*****"),
                Arguments.of("1234", "1234***********", "1234******"),
                Arguments.of("1", "1**************", "1*********")
        );
    }

    @ParameterizedTest
    @MethodSource("maskTests")
    void maskTests(String value, String expectedMasked5, String expectedMasked8, String expectedMasked2) {
        // Act
        var actualMasked5 = mask5(value);
        var actualMasked8 = mask8(value);
        var actualMasked2 = mask(value, 2);

        // Assert
        assertThat(actualMasked5).isEqualTo(expectedMasked5);
        assertThat(actualMasked8).isEqualTo(expectedMasked8);
        assertThat(actualMasked2).isEqualTo(expectedMasked2);
    }

    @ParameterizedTest
    @MethodSource("cutMaskTests")
    void cutMaskTests(String value, String expectedCut15Mask8, String expectedCutMask1) {
        // Act
        var actualCut15Mask8 = cut15Mask8(value);
        var actualCutMask1 = cutMask(value, 10, 5);

        var cutLength = randomIntegerGreaterThanZeroByBounds(5, 7);
        var maskLength = cutLength + randomIntegerGreaterThanZeroByBounds(0, 2);
        var throwable = catchThrowable(() -> cutMask(value, cutLength, maskLength));

        // Assert
        assertThat(throwable).isNotNull();
        assertThat(throwable.getClass()).isEqualTo(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).isEqualTo("`cutLength`=" + cutLength + " attribute must be greater than `maskLength`=" + maskLength);
        assertThat(actualCut15Mask8).isEqualTo(expectedCut15Mask8);
        assertThat(actualCutMask1).isEqualTo(expectedCutMask1);
    }
}
