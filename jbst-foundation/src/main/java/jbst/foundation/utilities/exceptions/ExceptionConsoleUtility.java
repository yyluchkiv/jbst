package jbst.foundation.utilities.exceptions;

import jbst.foundation.domain.asserts.ConsoleAsserts;
import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.base.PropertyName;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionConsoleUtility {

    // TODO [YYL] fixme?
    @Deprecated
    public static String invalidProperty(PropertyId propertyId) {
        return "Property \"%s\" is invalid".formatted(
                ConsoleAsserts.RED_TEXT.format(propertyId.value())
        );
    }

    public static String invalidProperty(PropertyName propertyName) {
        return "Property \"%s\" is invalid".formatted(
                ConsoleAsserts.RED_TEXT.format(propertyName.value())
        );
    }
}
