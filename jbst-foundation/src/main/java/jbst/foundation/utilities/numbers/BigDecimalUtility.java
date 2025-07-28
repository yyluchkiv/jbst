package jbst.foundation.utilities.numbers;

import jbst.foundation.domain.annotations.DeletionScheduled;
import jbst.foundation.domain.exceptions.base.JbstUnreachableCodeException;
import jbst.foundation.domain.tuples.TupleRange;
import jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility;
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
    private static final List<String> OPERATORS = List.of(">", ">=", "<", "<=");
    private static final String NUMBER1_PARAM = "number1";
    private static final String NUMBER2_PARAM = "number2";

    @DeletionScheduled(version = "1.8")
    public static boolean areValuesEquals(BigDecimal number1, BigDecimal number2) {
        if (isNull(number1) && isNull(number2)) {
            return true;
        }
        if (isNull(number1) || isNull(number2)) {
            return false;
        }
        return number1.compareTo(number2) == 0;
    }

    public static boolean is(@NotNull BigDecimal number1, @NotNull String operator, @NotNull BigDecimal number2) {
        assertTrueOrThrow(OPERATORS.contains(operator), "Available operators: " + OPERATORS);
        return switch (operator) {
            case ">" -> isFirstValueGreater(number1, number2);
            case ">=" -> isFirstValueGreaterOrEqual(number1, number2);
            case "<" -> isFirstValueLesser(number1, number2);
            case "<=" -> isFirstValueLesserOrEqual(number1, number2);
            default -> throw new JbstUnreachableCodeException();
        };
    }

    public static boolean isFirstValueGreater(BigDecimal number1, BigDecimal number2) {
        assertNonNullOrThrow(number1, ExceptionsMessagesUtility.invalidAttribute(NUMBER1_PARAM));
        assertNonNullOrThrow(number2, ExceptionsMessagesUtility.invalidAttribute(NUMBER2_PARAM));
        return number1.compareTo(number2) > 0;
    }

    public static boolean isFirstValueGreaterOrEqual(BigDecimal number1, BigDecimal number2) {
        assertNonNullOrThrow(number1, ExceptionsMessagesUtility.invalidAttribute(NUMBER1_PARAM));
        assertNonNullOrThrow(number2, ExceptionsMessagesUtility.invalidAttribute(NUMBER2_PARAM));
        return number1.compareTo(number2) >= 0;
    }

    public static boolean isFirstValueLesser(BigDecimal number1, BigDecimal number2) {
        assertNonNullOrThrow(number1, ExceptionsMessagesUtility.invalidAttribute(NUMBER1_PARAM));
        assertNonNullOrThrow(number2, ExceptionsMessagesUtility.invalidAttribute(NUMBER2_PARAM));
        return number1.compareTo(number2) < 0;
    }

    public static boolean isFirstValueLesserOrEqual(BigDecimal number1, BigDecimal number2) {
        assertNonNullOrThrow(number1, ExceptionsMessagesUtility.invalidAttribute(NUMBER1_PARAM));
        assertNonNullOrThrow(number2, ExceptionsMessagesUtility.invalidAttribute(NUMBER2_PARAM));
        return number1.compareTo(number2) <= 0;
    }

    public static boolean inRange(BigDecimal number, TupleRange<BigDecimal> range) {
        assertNonNullOrThrow(number, ExceptionsMessagesUtility.invalidAttribute("number"));
        assertNonNullOrThrow(range, ExceptionsMessagesUtility.invalidAttribute("range"));
        return isFirstValueGreater(number, range.from()) && isFirstValueLesser(number, range.to());
    }

    public static boolean inRangeClosed(BigDecimal number, TupleRange<BigDecimal> range) {
        assertNonNullOrThrow(number, ExceptionsMessagesUtility.invalidAttribute("number"));
        assertNonNullOrThrow(range, ExceptionsMessagesUtility.invalidAttribute("range"));
        return isFirstValueGreaterOrEqual(number, range.from()) && isFirstValueLesserOrEqual(number, range.to());
    }

    public static boolean isZero(BigDecimal number) {
        return areValuesEquals(number, ZERO);
    }

    public static boolean isNullOrZero(BigDecimal number) {
        return isNull(number) || isZero(number);
    }

    public static boolean isOneHundred(BigDecimal number) {
        return areValuesEquals(number, HUNDRED);
    }

    public static boolean isPositive(BigDecimal number) {
        return isFirstValueGreater(number, ZERO);
    }

    public static boolean isPositiveOrZero(BigDecimal number) {
        return isFirstValueGreaterOrEqual(number, ZERO);
    }

    public static boolean isNegative(BigDecimal number) {
        return isFirstValueLesser(number, ZERO);
    }

    public static boolean isNegativeOrZero(BigDecimal number) {
        return isFirstValueLesserOrEqual(number, ZERO);
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
