package jbst.foundation.domain.numbers;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static jbst.foundation.domain.constants.JbstConstants.Numbers.BigDecimals.*;
import static jbst.foundation.domain.numbers.BigDecimalUtility.*;

@UtilityClass
public class JbstNumbers {
    public static final int DEFAULT_SCALE = 3;
    public static final ConcurrentHashMap<Integer, DecimalFormat> DFS_BY_SCALE = new ConcurrentHashMap<>();

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
