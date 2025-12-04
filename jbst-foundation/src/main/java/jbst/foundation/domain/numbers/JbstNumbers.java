package jbst.foundation.domain.numbers;

import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.tuples.TupleRange;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static jbst.foundation.domain.asserts.JbstAsserts.assertNonNullOrThrow;
import static jbst.foundation.domain.asserts.JbstAsserts.assertTrueOrThrow;
import static jbst.foundation.domain.constants.JbstConstants.Numbers.BigDecimals.*;

@UtilityClass
public class JbstNumbers {
    public static final List<String> OPERATORS = List.of("==", ">", ">=", "<", "<=");
    public static final int DEFAULT_SCALE = 3;
    public static final ConcurrentHashMap<Integer, DecimalFormat> DFS_BY_SCALE = new ConcurrentHashMap<>();

    // =================================================================================================================
    // BIGDECIMAL(s)
    // =================================================================================================================
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
            default -> throw new JbstExceptions.UnreachableCode();
        };
    }

    public static boolean equalsApproximately(BigDecimal n1, BigDecimal n2, BigDecimal proximityPercent) {
        if (is(proximityPercent, "<", ZERO)) {
            throw new IllegalArgumentException("Proximity must be non-negative");
        }
        var diffAbs = n1.subtract(n2).abs();
        if (is(diffAbs, "==", ZERO)) {
            return true;
        }
        // |n1-n2| <= MAX(|n1|,|n2|)*proximity%
        return is(diffAbs, "<=", n1.abs().max(n2.abs()).multiply(divide(proximityPercent, HUNDRED, 10)));
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

    // =================================================================================================================
    // LONG(s)
    // =================================================================================================================
    public static int toIntExactOrZeroOnOverflow(long value) {
        try {
            return Math.toIntExact(value);
        } catch (ArithmeticException ex) {
            return 0;
        }
    }

    // =================================================================================================================
    // DIVISION(s)
    // =================================================================================================================
    public static BigDecimal divide(BigDecimal divider, BigDecimal divisor) {
        return divide(divider, divisor, DEFAULT_SCALE);
    }

    public static BigDecimal divide(BigDecimal divider, BigDecimal divisor, int scale) {
        return divider.divide(divisor, scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal divideOrZero(BigDecimal divider, BigDecimal divisor, int scale) {
        if (nonNull(divisor) && divisor.compareTo(ZERO) != 0) {
            return divider.divide(divisor, scale, RoundingMode.HALF_UP);
        } else {
            return ZERO;
        }
    }

    public static BigDecimal divideOrOne(BigDecimal divider, BigDecimal divisor, int scale) {
        try {
            if (nonNull(divisor) && divisor.compareTo(ZERO) != 0) {
                return divider.divide(divisor, scale, RoundingMode.HALF_UP);
            } else {
                return ONE;
            }
        } catch (RuntimeException ex) {
            return ONE;
        }
    }

    public static BigDecimal divideOrFallback(BigDecimal divider, BigDecimal divisor, int scale, BigDecimal fallback) {
        try {
            if (nonNull(divisor) && divisor.compareTo(ZERO) != 0) {
                return divider.divide(divisor, scale, RoundingMode.HALF_UP);
            } else {
                return fallback;
            }
        } catch (RuntimeException ex) {
            return fallback;
        }
    }

    // =================================================================================================================
    // READABILITY
    // =================================================================================================================
    public String getReadableNumber(BigDecimal number) {
        return getReadableNumber(number, 2);
    }

    public String getReadableNumber(BigDecimal number, int scale) {
        // N == 0
        if (isNull(number) || isZero(number)) {
            return scale(ZERO, scale).toString();
        }
        var positiveNumber = absOrZero(number);
        // -1K < N < 1K
        // N =< -1T
        // N >= 1T
        if (is(positiveNumber, "<", THOUSAND) || is(positiveNumber, ">=", TRILLION)) {
            return scale(number, scale).toString();
        }
        // N >= 1B
        if (is(positiveNumber, ">=", BILLION)) {
            return scale(divide(number, BILLION), 2).stripTrailingZeros() + "B";
        }
        // N >= 1M
        if (is(positiveNumber, ">=", MILLION)) {
            return scale(divide(number, MILLION), 2).stripTrailingZeros() + "M";
        }
        // N >= 1K
        return scale(divide(number, THOUSAND), 2).stripTrailingZeros() + "K";
    }

    // =================================================================================================================
    // FORMAT(s)
    // =================================================================================================================
    public static String format(BigDecimal value) {
        return format(value, DEFAULT_SCALE);
    }

    public static String format(BigDecimal value, int scale) {
        var symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(' ');
        DecimalFormat decimalFormat;
        if (DFS_BY_SCALE.containsKey(scale)) {
            decimalFormat = DFS_BY_SCALE.get(scale);
        } else {
            var pattern = "###,###." + IntStream.range(0, scale).mapToObj(i -> "#").collect(Collectors.joining());
            decimalFormat = new DecimalFormat(pattern, symbols);
            DFS_BY_SCALE.put(scale, decimalFormat);
        }
        return decimalFormat.format(value).replace(".", ",");
    }

    // =================================================================================================================
    // SCALING
    // =================================================================================================================
    public static BigDecimal scale(BigDecimal value) {
        return scale(value, DEFAULT_SCALE);
    }

    public static BigDecimal scale(BigDecimal value, int scale) {
        return value.setScale(scale, RoundingMode.HALF_UP);
    }
}
