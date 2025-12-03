package jbst.foundation.domain.hardware;

import jbst.foundation.domain.tuples.TuplePercentage;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static jbst.foundation.domain.constants.JbstConstants.Numbers.BigDecimals.HUNDRED;

@UtilityClass
public class JbstCPU {
    public static int getNumOfCores(TuplePercentage tuplePercentage) {
        return BigDecimal.valueOf(getNumOfCores()).multiply(tuplePercentage.percentage()).divide(
                HUNDRED,
                0,
                RoundingMode.DOWN
        ).intValue();
    }

    public static int getNumOfCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static int getHalfOfCores() {
        return Runtime.getRuntime().availableProcessors() * 5 / 10;
    }

}
