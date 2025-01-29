package jbst.foundation.utilities.colors;

import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AnsiUtility {

    public static AnsiFormat getBoldHexAnsiFormat(String hex) {
        var rgb = ColorUtility.hexToRgb(hex);
        return new AnsiFormat(Attribute.TEXT_COLOR(rgb[0], rgb[1], rgb[2]), Attribute.BOLD());
    }
}
