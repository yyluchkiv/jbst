package jbst.foundation.utilities.processors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static jbst.foundation.domain.constants.JbstConstants.Numbers.BigDecimals.HUNDRED;
import static jbst.foundation.domain.tests.constants.TestsJunitConstants.SMALL_ITERATIONS_COUNT;
import static jbst.foundation.domain.tuples.TuplePercentage.progressTuplePercentage;
import static jbst.foundation.utilities.processors.ProcessorsUtility.getHalfOfCores;
import static jbst.foundation.utilities.processors.ProcessorsUtility.getNumOfCores;
import static org.assertj.core.api.Assertions.assertThat;

class ProcessorsUtilityTest {

    @RepeatedTest(SMALL_ITERATIONS_COUNT)
    void getNumOfCoresTest() {
        // Act
        int cores = getNumOfCores();

        // Assert
        assertThat(cores).isPositive();
    }

    @RepeatedTest(SMALL_ITERATIONS_COUNT)
    void getHalfOfCoresTest() {
        // Act
        int cores = getHalfOfCores();

        // Assert
        assertThat(cores).isPositive();
    }

    @RepeatedTest(SMALL_ITERATIONS_COUNT)
    void integrationComparisonTest() {
        // Act
        int numOfCores = getNumOfCores();
        int halfOfCores = getHalfOfCores();

        // Assert
        assertThat(numOfCores).isPositive();
        assertThat(halfOfCores).isEqualTo(numOfCores * 5 / 10);
    }

    @Disabled
    @Test
    public void devTest() {
        // Arrange
        var _100 = progressTuplePercentage(new BigDecimal("100"), HUNDRED);
        var _88 = progressTuplePercentage(new BigDecimal("88"), HUNDRED);
        var _875 = progressTuplePercentage(new BigDecimal("87.5"), HUNDRED);
        var _87 = progressTuplePercentage(new BigDecimal("87"), HUNDRED);
        var _75 = progressTuplePercentage(new BigDecimal("75"), HUNDRED);
        var _25 = progressTuplePercentage(new BigDecimal("25"), HUNDRED);
        var _20 = progressTuplePercentage(new BigDecimal("20"), HUNDRED);
        var _15 = progressTuplePercentage(new BigDecimal("15"), HUNDRED);
        var _125 = progressTuplePercentage(new BigDecimal("12.5"), HUNDRED);
        var _124 = progressTuplePercentage(new BigDecimal("12.4"), HUNDRED);

        // Act
        System.out.println("numOfCores [100%]: " + getNumOfCores());
        System.out.println("numOfCores [_100]: " + getNumOfCores(_100));
        System.out.println("numOfCores [_88]: " + getNumOfCores(_88));
        System.out.println("numOfCores [_875]: " + getNumOfCores(_875));
        System.out.println("numOfCores [_87]: " + getNumOfCores(_87));
        System.out.println("numOfCores [_75]: " + getNumOfCores(_75));
        System.out.println("numOfCores [_25]: " + getNumOfCores(_25));
        System.out.println("numOfCores [_20]: " + getNumOfCores(_20));
        System.out.println("numOfCores [_15]: " + getNumOfCores(_15));
        System.out.println("numOfCores [_125]: " + getNumOfCores(_125));
        System.out.println("numOfCores [_124]: " + getNumOfCores(_124));
    }
}
