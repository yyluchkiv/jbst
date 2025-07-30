package jbst.foundation.utilities.numbers;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static jbst.foundation.domain.constants.JbstConstants.Numbers.BigDecimals.*;
import static jbst.foundation.utilities.numbers.BigDecimalUtility.*;
import static jbst.foundation.utilities.numbers.RoundingUtility.divide;
import static jbst.foundation.utilities.numbers.RoundingUtility.scale;

@UtilityClass
public class NumbersUtility {

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
}
