package jbst.foundation.domain.asserts;

import jbst.foundation.domain.reflections.JbstProperty;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.HashSet;

import static java.util.Objects.isNull;
import static jbst.foundation.domain.constants.JbstConstants.JColor.RED_TEXT;
import static jbst.foundation.utilities.collections.CollectionUtility.baseJoiningRaw;
import static org.apache.commons.collections4.SetUtils.disjunction;

@UtilityClass
public class ConsoleAsserts {
    public static void assertNonNullOrThrow(Object object, String propertyName) {
        if (isNull(object)) {
            throw new IllegalArgumentException(
                    "Property %s is null".formatted(
                            RED_TEXT.format(propertyName)
                    )
            );
        }
    }

    public static void assertNonEmptyOrThrow(Collection<?> collection, String propertyName) {
        if (collection.isEmpty()) {
            throw new IllegalArgumentException(
                    "Property %s is empty".formatted(
                            RED_TEXT.format(propertyName)
                    )
            );
        }
    }

    public static void assertNonNullPropertyOrThrow(JbstProperty jbstProperty) {
        if (isNull(jbstProperty)) {
            throw new IllegalArgumentException(RED_TEXT.format("Unknown reflection property"));
        }
        assertNonNullOrThrow(jbstProperty.getPropertyValue(), jbstProperty.getTreePropertyName());
    }

    @SuppressWarnings("unused")
    public static <T> void assertContainsAllOrThrow(Collection<T> options, Collection<T> required, String propertyName) {
        if (!options.containsAll(required)) {
            throw new IllegalArgumentException(
                    "%s. Options: [%s]. Required: [%s]. Disjunction: [%s]".formatted(
                            propertyName,
                            baseJoiningRaw(options),
                            baseJoiningRaw(required),
                            RED_TEXT.format(baseJoiningRaw(disjunction(new HashSet<>(options), new HashSet<>(required))))
                    )
            );
        }
    }

    @SuppressWarnings("unused")
    public static <T> void assertEqualsOrThrow(Collection<T> options, Collection<T> required, String propertyName) {
        if (!options.equals(required)) {
            throw new IllegalArgumentException(
                    "%s. Options: [%s]. Required: [%s]. Disjunction: [%s]".formatted(
                            propertyName,
                            baseJoiningRaw(options),
                            baseJoiningRaw(required),
                            RED_TEXT.format(baseJoiningRaw(disjunction(new HashSet<>(options), new HashSet<>(required))))
                    )
            );
        }
    }
}
