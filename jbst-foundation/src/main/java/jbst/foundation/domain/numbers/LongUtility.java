package jbst.foundation.domain.numbers;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LongUtility {

    public static int toIntExactOrZeroOnOverflow(long value) {
        try {
            return Math.toIntExact(value);
        } catch (ArithmeticException ex) {
            return 0;
        }
    }
}
