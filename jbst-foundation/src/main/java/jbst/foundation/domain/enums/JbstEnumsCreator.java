package jbst.foundation.domain.enums;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.function.Predicate;

import static jbst.foundation.domain.enums.JbstEnums.baseJoining;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.invalidAttributeOptionUnexpectedValue;

@UtilityClass
public class JbstEnumsCreator {
    // =================================================================================================================
    // THROWERS
    // =================================================================================================================

    public static <E extends Enum<E> & EnumValue<String>> E findEnumByValueIgnoreCaseOrThrow(Class<E> enumClass, String value) {
        Predicate<E> filter = e -> e.getValue().equalsIgnoreCase(value);
        return findEnumOrThrow(enumClass, value, filter);
    }

    public static <E extends Enum<E> & EnumValue<String>> E findEnumByNameOrThrow(Class<E> enumClass, String name) {
        Predicate<E> filter = e -> e.name().equals(name);
        return findEnumOrThrow(enumClass, name, filter);
    }

    // =================================================================================================================
    // FALLBACK: UNKNOWN
    // =================================================================================================================

    public static <E extends Enum<E> & EnumValue<String>> E findEnumByValueOrUnknown(Class<E> enumClass, String value) {
        return findEnumOrUnknown(enumClass, string -> string.equals(value));
    }

    public static <E extends Enum<E> & EnumValue<String>> E findEnumByValueIgnoreCaseOrUnknown(Class<E> enumClass, String value) {
        return findEnumOrUnknown(enumClass, string -> string.equalsIgnoreCase(value));
    }

    public static <E extends Enum<E> & EnumValue<Integer>> E findEnumByValueOrUnknown(Class<E> enumClass, int value) {
        return findEnumOrUnknown(enumClass, integer -> integer.equals(value));
    }

    public static <E extends Enum<E>> E findEnumByNameOrUnknown(Class<E> enumClass, String name) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.name().equals(name))
                .findFirst()
                .orElse(findUnknownValue(enumClass));
    }

    public static <E extends Enum<E>> E findUnknownValue(Class<E> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> "UNKNOWN".equals(e.name()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(enumClass.getSimpleName() + " does not have UNKNOWN enum value"));
    }

    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    public static <E extends Enum<E> & EnumValue<String>> E findEnumOrThrow(Class<E> enumClass, String param, Predicate<E> predicate) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(predicate)
                .findFirst()
                .orElseThrow(() -> {
                    var message = invalidAttributeOptionUnexpectedValue(
                            enumClass.getSimpleName(),
                            baseJoining(enumClass),
                            param
                    );
                    return new IllegalArgumentException(message);
                });
    }

    private static <E extends Enum<E> & EnumValue<V>, V> E findEnumOrUnknown(Class<E> enumClass, Predicate<V> predicate) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> predicate.test(e.getValue()))
                .findFirst()
                .orElse(findUnknownValue(enumClass));
    }
}
