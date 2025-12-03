package jbst.foundation.domain.asserts;

import jbst.foundation.domain.properties.JbstPropertyEdge;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static jbst.foundation.domain.constants.JbstConstants.JColor.RED_TEXT;
import static jbst.foundation.utilities.collections.JbstCollections.baseJoiningRaw;
import static org.apache.commons.collections4.SetUtils.disjunction;

@UtilityClass
public class ConsoleAsserts {
    public static final Map<Function<Class<?>, Boolean>, Consumer<JbstPropertyEdge>> PROPERTIES_ACTIONS = new HashMap<>();

    static {
        PROPERTIES_ACTIONS.put(Date.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        PROPERTIES_ACTIONS.put(LocalDate.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        PROPERTIES_ACTIONS.put(LocalDateTime.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        PROPERTIES_ACTIONS.put(ChronoUnit.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        PROPERTIES_ACTIONS.put(TimeUnit.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        PROPERTIES_ACTIONS.put(Boolean.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        PROPERTIES_ACTIONS.put(Short.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        PROPERTIES_ACTIONS.put(Integer.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        PROPERTIES_ACTIONS.put(Long.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        PROPERTIES_ACTIONS.put(BigInteger.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        PROPERTIES_ACTIONS.put(BigDecimal.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        PROPERTIES_ACTIONS.put(String.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        PROPERTIES_ACTIONS.put(Collection.class::isAssignableFrom, ConsoleAsserts::assertNonNullOrThrow);
    }

    public static void assertNonNullOrThrow(Object object, String propertyName) {
        if (isNull(object)) {
            throw new IllegalArgumentException(
                    "Property %s is null".formatted(
                            RED_TEXT.format(propertyName)
                    )
            );
        }
    }

    public static void assertNonNullOrThrow(JbstPropertyEdge edge) {
        if (isNull(edge)) {
            throw new IllegalArgumentException(RED_TEXT.format("Unknown reflection property"));
        }
        assertNonNullOrThrow(edge.getValueRAW(), edge.getName());
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
