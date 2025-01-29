package jbst.foundation.utilities.colors;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static jbst.foundation.utilities.colors.ColorUtility.hexToRgb;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ColorUtilityTest {

    private static Stream<Arguments> colors() {
        return Stream.of(
                Arguments.of("#FF5733", new int[]{255, 87, 51}),  // Red-Orange
                Arguments.of("#1E90FF", new int[]{30, 144, 255}), // Dodger Blue
                Arguments.of("#32CD32", new int[]{50, 205, 50}),  // Lime Green
                Arguments.of("#FFD700", new int[]{255, 215, 0}),  // Gold
                Arguments.of("#800080", new int[]{128, 0, 128})   // Purple
        );
    }

    @ParameterizedTest
    @MethodSource("colors")
    void hexToRgbTest(String hex, int[] expectedRgb) {
        // Act + Assert
        assertArrayEquals(expectedRgb, hexToRgb(hex));
    }
}
