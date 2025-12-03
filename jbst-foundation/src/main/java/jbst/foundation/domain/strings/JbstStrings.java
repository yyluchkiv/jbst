package jbst.foundation.domain.strings;

import jbst.foundation.domain.constants.JbstConstants;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import java.util.List;

import static java.lang.Character.isUpperCase;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@UtilityClass
public class JbstStrings {

    public static Object[] toObjectsArray(List<String> values) {
        return values.toArray(new String[0]);
    }

    public static boolean isNullOrEmpty(String value) {
        return isNull(value) || value.isEmpty();
    }

    public static boolean isNullOrBlank(String value) {
        return isNull(value) || value.isBlank();
    }

    public static boolean hasLength(String value) {
        return nonNull(value) && !value.isEmpty();
    }

    public static String getShortenValueOrUndefined(String value, int maxLength) {
        if (isNull(value)) {
            return JbstConstants.Strings.UNDEFINED;
        }
        if (value.length() <= 3) {
            return value;
        }
        if (maxLength <= 3) {
            maxLength = 3;
        }
        return (value.length() > maxLength) ? value.substring(0, maxLength - 3) + "..." : value;
    }

    public static String convertCamelCaseToSplit(String value) {
        if (!hasLength(value)) {
            return "";
        }
        var splitFormat = new StringBuilder();
        var firstElement = new StringBuilder();
        int j = value.length();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (isUpperCase(c) && i > 0) {
                j = i;
                break;
            }
            firstElement.append(c);
        }
        splitFormat.append(StringUtils.capitalize(firstElement.toString()));
        var restElements = new StringBuilder();
        value = StringUtils.uncapitalize(value.substring(j));
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (i == value.length() - 1) {
                restElements.insert(0, " ");
                restElements.append(String.valueOf(c).toLowerCase());
                splitFormat.append(restElements);
                break;
            }
            if (isUpperCase(c)) {
                restElements.insert(0, " ");
                splitFormat.append(restElements);
                restElements.setLength(0);
            }
            restElements.append(String.valueOf(c).toLowerCase());
        }
        return splitFormat.toString();
    }

    public static String toKebab(String value) {
        return StringUtils.uncapitalize(
                value.replaceAll("([a-z])([A-Z])", "$1-$2")
        ).toLowerCase();
    }
}
