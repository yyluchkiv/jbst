package jbst.foundation.domain.asserts;

import jbst.foundation.domain.base.PropertyId;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import static java.time.ZoneId.getAvailableZoneIds;
import static java.util.Objects.nonNull;
import static jbst.foundation.domain.numbers.BigDecimalUtility.*;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.hasLength;

@UtilityClass
public class JbstAsserts {

    // =================================================================================================================
    // 1 assert complexity: non null
    // =================================================================================================================
    public static void assertNonNullOrThrow(Object object, String message) {
        if (nonNull(object)) {
            return;
        }
        if (hasLength(message)) {
            throw new IllegalArgumentException(message);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static void assertNonNullOrThrow(Object object) {
        assertNonNullOrThrow(object, null);
    }

    // =================================================================================================================
    // 1 assert complexity: non blank
    // =================================================================================================================
    public static void assertNonBlankOrThrow(String object, String message) {
        if (!object.isBlank()) {
            return;
        }
        if (hasLength(message)) {
            throw new IllegalArgumentException(message);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @SuppressWarnings("unused")
    public static void assertNonBlankOrThrow(String object) {
        assertNonBlankOrThrow(object, null);
    }

    // =================================================================================================================
    // 1 assert complexity: has length
    // =================================================================================================================
    public static void assertHasLengthOrThrow(String object, String message) {
        if (hasLength(object)) {
            return;
        }
        if (hasLength(message)) {
            throw new IllegalArgumentException(message);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @SuppressWarnings("unused")
    public static void assertHasLengthOrThrow(String object) {
        assertHasLengthOrThrow(object, null);
    }

    // =================================================================================================================
    // 1 assert complexity: true/false
    // =================================================================================================================
    public static void assertTrueOrThrow(boolean condition, String message) {
        if (condition) {
            return;
        }
        if (hasLength(message)) {
            throw new IllegalArgumentException(message);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static void assertTrueOrThrow(boolean condition) {
        assertTrueOrThrow(condition, null);
    }

    public static void assertFalseOrThrow(boolean condition, String message) {
        if (!condition) {
            return;
        }
        if (hasLength(message)) {
            throw new IllegalArgumentException(message);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @SuppressWarnings("unused")
    public static void assertFalseOrThrow(boolean condition) {
        assertFalseOrThrow(condition, null);
    }

    // =================================================================================================================
    // 1 assert complexity: long
    // =================================================================================================================
    public static void assertPositiveOrThrow(long value, String message) {
        if (value > 0) {
            return;
        }
        if (hasLength(message)) {
            throw new IllegalArgumentException(message);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static void assertPositiveOrThrow(long value) {
        assertPositiveOrThrow(value, null);
    }

    public static void assertPositiveOrZeroOrThrow(long value, String message) {
        if (value >= 0) {
            return;
        }
        if (hasLength(message)) {
            throw new IllegalArgumentException(message);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static void assertPositiveOrZeroOrThrow(long value) {
        assertPositiveOrZeroOrThrow(value, null);
    }

    public static void assertNegativeOrThrow(long value, String message) {
        if (value < 0) {
            return;
        }
        if (hasLength(message)) {
            throw new IllegalArgumentException(message);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static void assertNegativeOrThrow(long value) {
        assertNegativeOrThrow(value, null);
    }

    public static void assertNegativeOrZeroOrThrow(long value, String message) {
        if (value <= 0) {
            return;
        }
        if (hasLength(message)) {
            throw new IllegalArgumentException(message);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static void assertNegativeOrZeroOrThrow(long value) {
        assertNegativeOrZeroOrThrow(value, null);
    }

    // =================================================================================================================
    // 1 assert complexity: BigDecimal
    // =================================================================================================================

    public static void assertPositiveOrThrow(BigDecimal value, String message) {
        if (isPositive(value)) {
            return;
        }
        if (hasLength(message)) {
            throw new IllegalArgumentException(message);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static void assertPositiveOrThrow(BigDecimal value) {
        assertPositiveOrThrow(value, null);
    }

    public static void assertPositiveOrZeroOrThrow(BigDecimal value, String message) {
        if (isPositiveOrZero(value)) {
            return;
        }
        if (hasLength(message)) {
            throw new IllegalArgumentException(message);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static void assertPositiveOrZeroOrThrow(BigDecimal value) {
        assertPositiveOrZeroOrThrow(value, null);
    }

    public static void assertNegativeOrThrow(BigDecimal value, String message) {
        if (isNegative(value)) {
            return;
        }
        if (hasLength(message)) {
            throw new IllegalArgumentException(message);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static void assertNegativeOrThrow(BigDecimal value) {
        assertNegativeOrThrow(value, null);
    }

    public static void assertNegativeOrZeroOrThrow(BigDecimal value, String message) {
        if (isNegativeOrZero(value)) {
            return;
        }
        if (hasLength(message)) {
            throw new IllegalArgumentException(message);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static void assertNegativeOrZeroOrThrow(BigDecimal value) {
        assertNegativeOrZeroOrThrow(value, null);
    }

    public static void assertFirstValueLesserOrThrow(BigDecimal value1, BigDecimal value2, String message) {
        if (is(value1, "<", value2)) {
            return;
        }
        if (hasLength(message)) {
            throw new IllegalArgumentException(message);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @SuppressWarnings("unused")
    public static void assertFirstValueLesserOrThrow(BigDecimal value1, BigDecimal value2) {
        assertFirstValueLesserOrThrow(value1, value2, null);
    }

    public static void assertFirstValueLesserOrEqualOrThrow(BigDecimal value1, BigDecimal value2, String message) {
        if (is(value1, "<=", value2)) {
            return;
        }
        if (hasLength(message)) {
            throw new IllegalArgumentException(message);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @SuppressWarnings("unused")
    public static void assertFirstValueLesserOrEqualOrThrow(BigDecimal value1, BigDecimal value2) {
        assertFirstValueLesserOrEqualOrThrow(value1, value2, null);
    }

    public static void assertBetweenExcludedOrThrow(BigDecimal value, BigDecimal left, BigDecimal right, String message) {
        if (is(left, "<", value) && is(value, "<", right)) {
            return;
        }
        if (hasLength(message)) {
            throw new IllegalArgumentException(message);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static void assertBetweenExcludedOrThrow(BigDecimal value, BigDecimal left, BigDecimal right) {
        assertBetweenExcludedOrThrow(value, left, right, null);
    }

    public static void assertBetweenIncludedOrThrow(BigDecimal value, BigDecimal left, BigDecimal right, String message) {
        if (is(left, "<=", value) && is(value, "<=", right)) {
            return;
        }
        if (hasLength(message)) {
            throw new IllegalArgumentException(message);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static void assertBetweenIncludedOrThrow(BigDecimal value, BigDecimal left, BigDecimal right) {
        assertBetweenIncludedOrThrow(value, left, right, null);
    }

    // =================================================================================================================
    // 1 assert complexity: collections
    // =================================================================================================================
    public static <T> void assertNonEmptyOrThrow(Collection<T> collection, String message) {
        if (!isEmpty(collection)) {
            return;
        }
        if (hasLength(message)) {
            throw new IllegalArgumentException(message);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @SuppressWarnings("unused")
    public static <T> void assertNonEmptyOrThrow(Collection<T> collection) {
        assertNonEmptyOrThrow(collection, null);
    }

    public static <K, V> void assertNonEmptyOrThrow(Map<K, V> collection, String message) {
        if (!isEmpty(collection)) {
            return;
        }
        if (hasLength(message)) {
            throw new IllegalArgumentException(message);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @SuppressWarnings("unused")
    public static <K, V> void assertNonEmptyOrThrow(Map<K, V> map) {
        assertNonEmptyOrThrow(map, null);
    }

    public static <T> void assertSortedOrThrow(Collection<T> collection, Comparator<T> comparator, String message) {
        var iterator = collection.iterator();
        if (!iterator.hasNext()) {
            return;
        }
        T current = iterator.next();
        while (iterator.hasNext()) {
            T next = iterator.next();
            if (comparator.compare(current, next) > 0) {
                if (hasLength(message)) {
                    throw new IllegalArgumentException(message);
                } else {
                    throw new IllegalArgumentException();
                }
            }
            current = next;
        }
    }

    public static <T> void assertSortedOrThrow(Collection<T> collection, Comparator<T> comparator) {
        assertSortedOrThrow(collection, comparator, null);
    }

    public static <T> void assertUniqueOrThrow(Collection<T> options, T object, PropertyId propertyId) {
        if (options.contains(object)) {
            throw new IllegalArgumentException(
                    "Property %s must be unique".formatted(propertyId.value())
            );
        }
    }
    // =================================================================================================================
    // 1 assert complexity: require
    // =================================================================================================================
    public static <T> T requireNonNullOrThrow(T object, String message) {
        if (nonNull(object)) {
            return object;
        }
        if (hasLength(message)) {
            throw new IllegalArgumentException(message);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @SuppressWarnings("unused")
    public static <T> T requireNonNullOrThrow(T object) {
        return requireNonNullOrThrow(object, null);
    }

    // =================================================================================================================
    // 1+ asserts complexity
    // =================================================================================================================
    public static void assertZoneIdOrThrow(String zoneId, String message) {
        assertHasLengthOrThrow(zoneId, message);
        if (!getAvailableZoneIds().contains(zoneId)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void assertDateTimePatternOrThrow(String dateTimePattern, String message) {
        assertHasLengthOrThrow(dateTimePattern, message);
        assertNonNullOrThrow(DateTimeFormatter.ofPattern(dateTimePattern), message);
    }
}
