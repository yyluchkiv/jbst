package jbst.foundation.domain.colors;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JbstColors {

    public static int[] hexToRgb(String hex) {
        hex = hex.replace("#", "");
        return new int[] {
                Integer.parseInt(hex.substring(0, 2), 16),
                Integer.parseInt(hex.substring(2, 4), 16),
                Integer.parseInt(hex.substring(4, 6), 16)
        };
    }
}
