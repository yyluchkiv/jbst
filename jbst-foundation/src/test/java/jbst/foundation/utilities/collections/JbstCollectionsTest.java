package jbst.foundation.utilities.collections;

import jbst.foundation.domain.tests.constants.TestsJunitConstants;
import jbst.foundation.domain.tests.enums.EnumUnderTests;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jbst.foundation.domain.tests.constants.TestsJunitConstants.FIVE_TIMES;
import static jbst.foundation.utilities.collections.JbstCollections.*;
import static jbst.foundation.utilities.random.RandomUtility.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class JbstCollectionsTest {

    private static Stream<Arguments> getFirstNElementsTest() {
        return Stream.of(
                Arguments.of(List.of(0, 1, 2, 3, 4), 2, List.of(0, 1)),
                Arguments.of(List.of(0, 1, 2, 3, 4), 10, List.of(0, 1, 2, 3, 4)),
                Arguments.of(List.of(0, 1, 2, 3, 4), 5, List.of(0, 1, 2, 3, 4)),
                Arguments.of(List.of(), 5, List.of()),
                Arguments.of(List.of(0, 1, 2, 3, 4), 0, List.of())
        );
    }

    private static Stream<Arguments> getLastNElementsTest() {
        return Stream.of(
                Arguments.of(List.of(0, 1, 2, 3, 4), 2, List.of(3, 4)),
                Arguments.of(List.of(0, 1, 2, 3, 4), 10, List.of(0, 1, 2, 3, 4)),
                Arguments.of(List.of(0, 1, 2, 3, 4), 5, List.of(0, 1, 2, 3, 4)),
                Arguments.of(List.of(), 5, List.of()),
                Arguments.of(List.of(0, 1, 2, 3, 4), 0, List.of())
        );
    }

    private static Stream<Arguments> maxOrZeroTest() {
        return Stream.of(
                Arguments.of(null, BigDecimal.ZERO),
                Arguments.of(List.of(), BigDecimal.ZERO),
                Arguments.of(List.of(new BigDecimal(0)), BigDecimal.ZERO),
                Arguments.of(List.of(new BigDecimal(-100), new BigDecimal(-1), new BigDecimal(-5)), new BigDecimal(-1)),
                Arguments.of(List.of(new BigDecimal(4), new BigDecimal(9), new BigDecimal(18)), new BigDecimal(18))
        );
    }

    private static Stream<Arguments> minOrZeroTest() {
        return Stream.of(
                Arguments.of(null, BigDecimal.ZERO),
                Arguments.of(List.of(), BigDecimal.ZERO),
                Arguments.of(List.of(new BigDecimal(0)), BigDecimal.ZERO),
                Arguments.of(List.of(new BigDecimal(-100), new BigDecimal(-1), new BigDecimal(-5)), new BigDecimal(-100)),
                Arguments.of(List.of(new BigDecimal(4), new BigDecimal(9), new BigDecimal(18)), new BigDecimal(4))
        );
    }

    @Test
    void toConcurrentSetTest() {
        // Arrange
        var collection = List.of(
                BigDecimal.valueOf(38.43),
                BigDecimal.valueOf(58.455555),
                BigDecimal.valueOf(77.3392543),
                BigDecimal.valueOf(8.99999999)
        );

        // Act
        var actual = toConcurrentSet(collection);

        // Assert
        assertThat(actual)
                .isNotNull()
                .containsAll(collection)
                .isExactlyInstanceOf(ConcurrentHashMap.KeySetView.class);
    }

    @Test
    void mutableSingletonListIntegrationTest() {
        // Arrange
        var element1 = "element1";
        var element2 = "element2";
        var element3 = "element3";

        // Act-1
        var elements = mutableSingletonList(element1);

        // Assert-1
        assertThat(elements).hasSize(1);
        assertThat(elements.get(0)).isEqualTo(element1);

        // Act-2
        elements.add(element2);
        elements.add(element3);

        // Assert-2
        assertThat(elements)
                .hasSize(3)
                .containsExactly(element1, element2, element3);
    }

    @Test
    void mutableSingletonSetIntegrationTest() {
        // Arrange
        var element1 = "element1";
        var element2 = "element2";
        var element3 = "element3";

        // Act-1
        var elements = mutableSingletonSet(element1);

        // Assert-1
        assertThat(elements).hasSize(1);
        assertThat(elements.iterator().next()).isEqualTo(element1);

        // Act-2
        elements.add(element2);
        elements.add(element3);

        // Assert-2
        assertThat(elements)
                .hasSize(3)
                .containsExactly(element1, element2, element3);
    }

    @Test
    void mutableSingletonMapIntegrationTest() {
        // Arrange
        var key1 = "key1";
        var key2 = "key2";
        var key3 = "key3";
        var value1 = "value1";
        var value2 = "value2";
        var value3 = "value3";

        // Act-1
        var elements = mutableSingletonMap(key1, value1);

        // Assert-1
        assertThat(elements).hasSize(1);
        var entry = elements.entrySet().iterator().next();
        assertThat(entry.getKey()).isEqualTo(key1);
        assertThat(entry.getValue()).isEqualTo(value1);

        // Act-2
        elements.put(key2, value2);
        elements.put(key3, value3);

        // Assert-2
        assertThat(elements).hasSize(3);
        assertThat(elements.keySet()).containsExactly(key1, key2, key3);
        assertThat(elements.values()).containsExactly(value1, value2, value3);
    }

    @Test
    void emptyQueueTest() {
        // Arrange
        var expected = new LinkedList<>();

        // Act
        var actual = emptyQueue();

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void unionListsTest() {
        // Arrange
        var list1 = randomStringsAsList(randomIntegerGreaterThanZeroByBounds(3, 10));
        var list2 = randomStringsAsList(randomIntegerGreaterThanZeroByBounds(3, 10));

        // Act
        var actual = union(list1, list2);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getClass()).isEqualTo(ArrayList.class);
        assertThat(actual)
                .hasSize(list1.size() + list2.size())
                .containsAll(list1)
                .containsAll(list2);
    }

    @Test
    void unionSetTest() {
        // Arrange
        var set1 = randomStringsAsSet(randomIntegerGreaterThanZeroByBounds(3, 10));
        var set2 = randomStringsAsSet(randomIntegerGreaterThanZeroByBounds(3, 10));

        // Act
        var actual = union(set1, set2);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getClass()).isEqualTo(HashSet.class);
        assertThat(actual)
                .hasSize(set1.size() + set2.size())
                .containsAll(set1)
                .containsAll(set2);
    }

    @Test
    void safeGettersExceptionCases() {
        // Arrange
        var emptyList = List.of();
        var emptySet = Set.of();

        var throwable1 = catchThrowable(() -> safeFirstElement(emptyList));
        var throwable2 = catchThrowable(() -> safeFirstElement(emptySet));
        var throwable3 = catchThrowable(() -> safeLastElement(emptyList));
        var throwable4 = catchThrowable(() -> safeLastElement(emptyList));

        // Assert
        List.of(throwable1, throwable2, throwable3, throwable4).forEach(throwable -> {
            assertThat(throwable).isNotNull();
            assertThat(throwable).isInstanceOf(NullPointerException.class);
        });
    }

    @RepeatedTest(TestsJunitConstants.SMALL_ITERATIONS_COUNT)
    void safeGetters() {
        // Arrange
        var list = List.of(1, 2, 3, 4, 5);
        var set = new LinkedHashSet<>();
        set.add(1);
        set.add(2);
        set.add(3);
        set.add(4);
        set.add(5);

        var first1 = safeFirstElement(list);
        var first2 = safeFirstElement(set);
        var last1 = safeLastElement(list);
        var last2 = safeLastElement(set);

        // Assert
        List.of(first1, first2).forEach(first -> {
            assertThat(first).isNotNull();
            assertThat(first).isEqualTo(1);
        });
        List.of(last1, last2).forEach(last -> {
            assertThat(last).isNotNull();
            assertThat(last).isEqualTo(5);
        });
    }

    @ParameterizedTest
    @MethodSource("getFirstNElementsTest")
    void getFirstNElementsTest(List<Integer> list, int size, List<Integer> expected) {
        // Act
        var actual = getFirstNElements(list, size);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getFirstNElementsExceptionTest() {
        // Arrange
        var list = List.of(0, 1, 2, 3, 4);

        // Act
        var throwable = catchThrowable(() -> getFirstNElements(list, randomIntegerLessThanZero()));

        // Assert
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Elements quantity can't be negative");
    }

    @ParameterizedTest
    @MethodSource("getLastNElementsTest")
    void getLastNElementsTest(List<Integer> list, int size, List<Integer> expected) {
        // Act
        var actual = getLastNElements(list, size);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getLastNElementsExceptionTest() {
        // Arrange
        var list = List.of(0, 1, 2, 3, 4);

        // Act
        var throwable = catchThrowable(() -> getLastNElements(list, randomIntegerLessThanZero()));

        // Assert
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Elements quantity can't be negative");
    }

    @ParameterizedTest
    @MethodSource("maxOrZeroTest")
    void maxOrZeroTest(List<BigDecimal> list, BigDecimal expected) {
        // Act
        var actual = maxOrZero(list);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("minOrZeroTest")
    void minOrZeroTest(List<BigDecimal> list, BigDecimal expected) {
        // Act
        var actual = minOrZero(list);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @RepeatedTest(FIVE_TIMES)
    void baseJoiningRawTest() {
        // Act
        var actual = baseJoiningRaw(Stream.of(EnumUnderTests.values()).collect(Collectors.toSet()));

        // Assert
        assertThat(actual).isEqualTo("EXAMPLE_1, EXAMPLE_2, EXAMPLE_3, EXAMPLE_4");
    }
}
