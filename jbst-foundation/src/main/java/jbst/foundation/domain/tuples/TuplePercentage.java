package jbst.foundation.domain.tuples;

import jbst.foundation.domain.annotations.DeletionScheduled;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static jbst.foundation.domain.constants.JbstConstants.Numbers.BigDecimals.HUNDRED;
import static jbst.foundation.utilities.numbers.RoundingUtility.divideOrZero;
import static jbst.foundation.utilities.numbers.RoundingUtility.scale;

public record TuplePercentage(
        BigDecimal value,
        BigDecimal percentage
) {

    public static TuplePercentage of(BigDecimal value, BigDecimal maxValue, int valueScale, int percentageScale) {
        return new TuplePercentage(
                scale(value, valueScale),
                divideOrZero(value.abs().multiply(HUNDRED), maxValue, percentageScale)
        );
    }

    public static TuplePercentage progressTuplePercentage(BigDecimal value, BigDecimal maxValue) {
        return of(value, maxValue, 2, 2);
    }

    public static TuplePercentage progressTuplePercentage(long value, long maxValue) {
        return progressTuplePercentage(new BigDecimal(value), new BigDecimal(maxValue));
    }

    public static TuplePercentage zero() {
        return progressTuplePercentage(ZERO, HUNDRED);
    }

    public static TuplePercentage hundred() {
        return progressTuplePercentage(HUNDRED, HUNDRED);
    }

    @DeletionScheduled(version = "1.8")
    public static TuplePercentage oneHundred() {
        return progressTuplePercentage(HUNDRED, HUNDRED);
    }
}
