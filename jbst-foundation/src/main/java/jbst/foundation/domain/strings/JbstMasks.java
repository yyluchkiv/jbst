package jbst.foundation.domain.strings;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JbstMasks {

    public static String mask5(String value) {
        return mask(value, 5);
    }

    public static String mask8(String value) {
        return mask(value, 8);
    }

    public static String mask(String value, int length) {
        int valueLength = value.length();
        if (valueLength <= length) {
            return value;
        }
        return value.substring(0, length) + "*".repeat(valueLength - length);
    }

    public static String cut15Mask8(String value) {
        return cutMask(value, 15, 8);
    }

    public static String cutMask(String value, int cutLength, int maskLength) {
        if (cutLength <= maskLength) {
            throw new IllegalArgumentException("`cutLength`=" + cutLength + " attribute must be greater than `maskLength`=" + maskLength);
        }
        int valueLength = value.length();
        if (valueLength < cutLength) {
            value = value + "*".repeat(cutLength - valueLength);
        } else {
            value = value.substring(0, cutLength);
        }
        return mask(value, maskLength);
    }
}
