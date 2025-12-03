package jbst.foundation.domain.colors;

import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JbstANSI {

    public static AnsiFormat getBoldHexAnsiFormat(String hex) {
        var rgb = JbstColors.hexToRgb(hex);
        return new AnsiFormat(Attribute.TEXT_COLOR(rgb[0], rgb[1], rgb[2]), Attribute.BOLD());
    }
}
