package jbst.foundation.domain.enums;

import jbst.foundation.domain.tests.JbstUnitTests.Enums.EnumNoValuesUnderTests;
import jbst.foundation.domain.tests.JbstUnitTests.Enums.EnumOneValueUnderTests;
import jbst.foundation.domain.tests.JbstUnitTests.Enums.EnumUnderTests;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static jbst.foundation.domain.enums.JbstEnums.*;
import static jbst.foundation.domain.tests.JbstUnitTests.Enums.EnumOneValueUnderTests.ONE_VALUE;
import static jbst.foundation.domain.tests.JbstUnitTests.Enums.EnumUnderTests.*;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({"unchecked", "rawtypes"})
class JbstEnumsTest {

    private static Stream<Arguments> getEnumNamesTest() {
        return Stream.of(
                Arguments.of(EnumNoValuesUnderTests.class, Set.of()),
                Arguments.of(EnumOneValueUnderTests.class, Set.of("ONE_VALUE")),
                Arguments.of(EnumUnderTests.class, Set.of("EXAMPLE_1", "EXAMPLE_2", "EXAMPLE_3", "EXAMPLE_4"))
        );
    }

    private static Stream<Arguments> setTest() {
        return Stream.of(
                Arguments.of(EnumNoValuesUnderTests.class, Set.of()),
                Arguments.of(EnumOneValueUnderTests.class, Set.of(ONE_VALUE)),
                Arguments.of(EnumUnderTests.class, Set.of(EXAMPLE_1, EXAMPLE_2, EXAMPLE_3, EXAMPLE_4))
        );
    }

    private static Stream<Arguments> baseJoiningAsClassTest() {
        return Stream.of(
                Arguments.of(EnumNoValuesUnderTests.class, ""),
                Arguments.of(EnumOneValueUnderTests.class, "ONE_VALUE"),
                Arguments.of(EnumUnderTests.class, "EXAMPLE_1, EXAMPLE_2, EXAMPLE_3, EXAMPLE_4")
        );
    }

    private static Stream<Arguments> baseJoiningAsSetTest() {
        return Stream.of(
                Arguments.of(set(EnumNoValuesUnderTests.class), ""),
                Arguments.of(set(EnumOneValueUnderTests.class), "ONE_VALUE"),
                Arguments.of(set(EnumUnderTests.class), "EXAMPLE_1, EXAMPLE_2, EXAMPLE_3, EXAMPLE_4")
        );
    }

    @ParameterizedTest
    @MethodSource("getEnumNamesTest")
    void getEnumNamesTest(Class<? extends Enum> enumClass, Set<String> expected) {
        // Act
        var actual = getEnumNames(enumClass);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("setTest")
    void setTest(Class<? extends Enum> enumClass, Set<? extends Enum> expected) {
        // Act
        var actual = set(enumClass);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("baseJoiningAsClassTest")
    void baseJoiningAsClassTest(Class<? extends Enum> enumClass, String expected) {
        // Act
        var actual = baseJoining(enumClass);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("baseJoiningAsSetTest")
    void baseJoiningAsSetTest(Set<? extends Enum> enumsSet, String expected) {
        // Act
        var actual = baseJoining(enumsSet);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
