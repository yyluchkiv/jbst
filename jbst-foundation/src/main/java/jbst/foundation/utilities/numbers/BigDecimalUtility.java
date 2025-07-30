package jbst.foundation.utilities.numbers;

import jbst.foundation.domain.exceptions.base.JbstUnreachableCodeException;
import jbst.foundation.domain.tuples.TupleRange;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static jbst.foundation.domain.asserts.Asserts.assertNonNullOrThrow;
import static jbst.foundation.domain.asserts.Asserts.assertTrueOrThrow;
import static jbst.foundation.domain.constants.JbstConstants.Numbers.BigDecimals.HUNDRED;

@UtilityClass
public class BigDecimalUtility {
    private static final List<String> OPERATORS = List.of("==", ">", ">=", "<", "<=");

    public static boolean is(BigDecimal number1, @NotNull String operator, BigDecimal number2) {
        assertTrueOrThrow(OPERATORS.contains(operator), "Available operators: " + OPERATORS);
        return switch (operator) {
            case "==" -> {
                if (isNull(number1) || isNull(number2)) {
                    yield false;
                }
                yield number1.compareTo(number2) == 0;
            }
            case ">" -> {
                assertNonNullOrThrow(number1);
                assertNonNullOrThrow(number2);
                yield number1.compareTo(number2) > 0;
            }
            case ">=" -> {
                assertNonNullOrThrow(number1);
                assertNonNullOrThrow(number2);
                yield number1.compareTo(number2) >= 0;
            }
            case "<" -> {
                assertNonNullOrThrow(number1);
                assertNonNullOrThrow(number2);
                yield number1.compareTo(number2) < 0;
            }
            case "<=" -> {
                assertNonNullOrThrow(number1);
                assertNonNullOrThrow(number2);
                yield number1.compareTo(number2) <= 0;
            }
            default -> throw new JbstUnreachableCodeException();
        };
    }

    public static boolean inRange(@NotNull BigDecimal number, @NotNull TupleRange<BigDecimal> range) {
        return is(number, ">", range.from()) && is(number, "<", range.to());
    }

    public static boolean inRangeClosed(@NotNull BigDecimal number, @NotNull TupleRange<BigDecimal> range) {
        return is(number, ">=", range.from()) && is(number, "<=", range.to());
    }

    public static boolean isZero(BigDecimal number) {
        return is(number, "==", ZERO);
    }

    public static boolean isNullOrZero(BigDecimal number) {
        return isNull(number) || isZero(number);
    }

    public static boolean isHundred(BigDecimal number) {
        return is(number, "==", HUNDRED);
    }

    public static boolean isPositive(BigDecimal number) {
        return is(number, ">", ZERO);
    }

    public static boolean isPositiveOrZero(BigDecimal number) {
        return is(number, ">=", ZERO);
    }

    public static boolean isNegative(BigDecimal number) {
        return is(number, "<", ZERO);
    }

    public static boolean isNegativeOrZero(BigDecimal number) {
        return is(number, "<=", ZERO);
    }

    public static BigDecimal absOrZero(BigDecimal number) {
        if (isNull(number)) {
            return ZERO;
        } else {
            if (isPositiveOrZero(number)) {
                return number;
            } else {
                return number.negate();
            }
        }
    }

    public static int getNumberOfDigitsAfterTheDecimalPointOrZero(BigDecimal number) {
        return Math.max(0, number.stripTrailingZeros().scale());
    }

    public static int getNumberOfDigitsAfterTheDecimalPointIncludingTrailingZerosOrZero(BigDecimal number) {
        return Math.max(0, number.scale());
    }
}
