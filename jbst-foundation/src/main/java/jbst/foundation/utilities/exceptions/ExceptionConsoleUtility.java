package jbst.foundation.utilities.exceptions;

import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.constants.JbstConstants;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionConsoleUtility {

    // TODO [YYL] fixme?
    @Deprecated
    public static String invalidProperty(PropertyId propertyId) {
        return "Property \"%s\" is invalid".formatted(
                JbstConstants.JColor.RED_TEXT.format(propertyId.value())
        );
    }
}
