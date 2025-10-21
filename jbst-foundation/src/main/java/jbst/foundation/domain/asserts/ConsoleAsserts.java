package jbst.foundation.domain.asserts;

import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.reflections.ReflectionProperty;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.HashSet;

import static java.util.Objects.isNull;
import static jbst.foundation.utilities.collections.CollectionUtility.baseJoiningRaw;
import static jbst.foundation.utilities.exceptions.ExceptionConsoleUtility.invalidProperty;
import static org.apache.commons.collections4.SetUtils.disjunction;

@UtilityClass
public class ConsoleAsserts {
    public static void assertNonNullOrThrow(Object object, String propertyName) {
        if (isNull(object)) {
            throw new IllegalArgumentException(
                    "Property '%s' is invalid".formatted(
                            JbstConstants.JColor.RED_TEXT.format(propertyName)
                    )
            );
        }
    }

    @Deprecated
    public static void assertNonNullOrThrow(Object object, PropertyId propertyId) {
        if (isNull(object)) {
            throw new IllegalArgumentException(invalidProperty(propertyId));
        }
    }

    public static void assertNonEmptyOrThrow(Collection<?> collection, PropertyId propertyId) {
        if (collection.isEmpty()) {
            throw new IllegalArgumentException(invalidProperty(propertyId));
        }
    }

    public static void assertNonNullNotEmptyOrThrow(Collection<?> collection, PropertyId propertyId) {
        assertNonNullOrThrow(collection, propertyId);
        assertNonEmptyOrThrow(collection, propertyId);
    }

    public static void assertNonNullPropertyOrThrow(ReflectionProperty reflectionProperty) {
        if (isNull(reflectionProperty)) {
            throw new IllegalArgumentException(JbstConstants.JColor.RED_TEXT.format("Unknown reflection property"));
        }
        assertNonNullOrThrow(reflectionProperty.getPropertyValue(), reflectionProperty.getTreePropertyId());
    }

    @SuppressWarnings("unused")
    public static <T> void assertContainsAllOrThrow(Collection<T> options, Collection<T> required, PropertyId propertyId) {
        if (!options.containsAll(required)) {
            throw new IllegalArgumentException(
                    "%s. Options: '[%s]'. Required: '[%s]'. Disjunction: '[%s]'".formatted(
                            invalidProperty(propertyId),
                            baseJoiningRaw(options),
                            baseJoiningRaw(required),
                            JbstConstants.JColor.RED_TEXT.format(baseJoiningRaw(disjunction(new HashSet<>(options), new HashSet<>(required))))
                    )
            );
        }
    }

    @SuppressWarnings("unused")
    public static <T> void assertEqualsOrThrow(Collection<T> options, Collection<T> required, PropertyId propertyId) {
        if (!options.equals(required)) {
            throw new IllegalArgumentException(
                    "%s. Options: '[%s]'. Required: '[%s]'. Disjunction: '[%s]'".formatted(
                            invalidProperty(propertyId),
                            baseJoiningRaw(options),
                            baseJoiningRaw(required),
                            JbstConstants.JColor.RED_TEXT.format(baseJoiningRaw(disjunction(new HashSet<>(options), new HashSet<>(required))))
                    )
            );
        }
    }
}
