package jbst.foundation.domain.enums;

import jbst.foundation.domain.constants.JbstConstants;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class JbstEnums {
    public static <E extends Enum<E>> Set<String> getEnumNames(@NotNull Class<E> enumClass) {
        return EnumSet.allOf(enumClass).stream().map(Enum::name).collect(Collectors.toSet());
    }

    public static <E extends Enum<E>> Set<E> set(Class<E> enumClass) {
        return Stream.of(enumClass.getEnumConstants()).collect(Collectors.toSet());
    }

    @SuppressWarnings("rawtypes")
    public static Set setWildcard(Class<? extends Enum<?>> enumClass) {
        return Stream.of(enumClass.getEnumConstants()).collect(Collectors.toSet());
    }

    public static <E extends Enum<E>> String baseJoining(Class<E> enumClass) {
        return Stream.of(enumClass.getEnumConstants()).map(Enum::toString).sorted().collect(Collectors.joining(JbstConstants.Symbols.COLLECTORS_COMMA_SPACE));
    }

    public static <E extends Enum<E>> String baseJoining(Set<E> enums) {
        return enums.stream().map(Enum::toString).sorted().collect(Collectors.joining(JbstConstants.Symbols.COLLECTORS_COMMA_SPACE));
    }

    public static String baseJoiningWildcard(Class<? extends Enum<?>> enumClass) {
        return Stream.of(enumClass.getEnumConstants()).map(Enum::toString).sorted().collect(Collectors.joining(JbstConstants.Symbols.COLLECTORS_COMMA_SPACE));
    }
}
