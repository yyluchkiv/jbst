package jbst.foundation.domain.strings;

import lombok.experimental.UtilityClass;

import static org.springframework.util.StringUtils.capitalize;

@UtilityClass
public class JbstMessages {

    public static String pleaseWait(String prefix) {
        return "%s. Please wait...".formatted(prefix);
    }

    public static String contactDevelopmentTeam(String prefix) {
        return "%s. Please contact development team".formatted(prefix);
    }

    public static String unexpectedErrorOccurred() {
        return contactDevelopmentTeam("An unexpected error occurred");
    }

    public static String notImplementedYet() {
        return contactDevelopmentTeam("Not implemented yet");
    }

    public static String invalidAttribute(String attributeName) {
        return "Attribute `%s` is invalid".formatted(attributeName);
    }

    public static String invalidAttributeOptionUnexpectedValue(String attributeName, String options, String unexpected) {
        return "Attribute `%s` is invalid. Options: `[%s]`. Unexpected: `[%s]`".formatted(attributeName, options, unexpected);
    }

    public static String entityNotFound(String entityType, String entity) {
        return "%s %s is not found".formatted(capitalize(entityType.toLowerCase()), entity);
    }

    public static String entityAlreadyUsed(String entityType, String entity) {
        return "%s %s is already used".formatted(capitalize(entityType.toLowerCase()), entity);
    }

    public static String entityAccessDenied(String entityType, String entity) {
        return "%s %s access denied".formatted(capitalize(entityType.toLowerCase()), entity);
    }
}
