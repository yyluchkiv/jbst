package jbst.foundation.domain.properties.utilities;

import jbst.foundation.domain.asserts.ConsoleAsserts;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.annotations.MandatoryMapProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryToggleProperty;
import jbst.foundation.domain.properties.annotations.NonMandatoryProperty;
import jbst.foundation.domain.properties.base.AbstractPropertyConfigs;
import jbst.foundation.domain.properties.base.AbstractTogglePropertyConfigs;
import jbst.foundation.domain.properties.configs.AbstractPropertiesConfigs;
import jbst.foundation.domain.reflections.JbstProperty;
import jbst.foundation.utilities.enums.EnumUtility;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static jbst.foundation.domain.asserts.Asserts.assertTrueOrThrow;
import static jbst.foundation.domain.asserts.ConsoleAsserts.assertNonNullOrThrow;
import static jbst.foundation.utilities.collections.CollectionUtility.baseJoiningRaw;
import static jbst.foundation.utilities.enums.EnumUtility.baseJoining;
import static jbst.foundation.utilities.enums.EnumUtility.baseJoiningWildcard;
import static org.apache.commons.collections4.SetUtils.disjunction;

@Slf4j
@UtilityClass
public class PropertiesAsserter {
    private static final Map<Function<Class<?>, Boolean>, Consumer<JbstProperty>> ACTIONS = new HashMap<>();

    static {
        ACTIONS.put(Date.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        ACTIONS.put(LocalDate.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        ACTIONS.put(LocalDateTime.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        ACTIONS.put(ChronoUnit.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        ACTIONS.put(TimeUnit.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        ACTIONS.put(Boolean.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        ACTIONS.put(Short.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        ACTIONS.put(Integer.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        ACTIONS.put(Long.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        ACTIONS.put(BigInteger.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        ACTIONS.put(BigDecimal.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        ACTIONS.put(String.class::equals, ConsoleAsserts::assertNonNullOrThrow);
        ACTIONS.put(Collection.class::isAssignableFrom, property -> {
            var collection = (Collection<?>) property.getPropertyValue();
            ConsoleAsserts.assertNonNullOrThrow(collection, property.getPropertyName());
            ConsoleAsserts.assertNonEmptyOrThrow(collection, property.getPropertyName());
        });
    }

    // =================================================================================================================
    // Assertions
    // =================================================================================================================
    public static void assertMandatoryPropertyConfigs(AbstractPropertyConfigs propertyConfigs, String propertyName) {
        assertNonNullOrThrow(propertyConfigs, propertyName);
        assertPropertyConfigs(
                propertyConfigs,
                propertyName,
                getMandatoryFields(propertyConfigs, propertyName)
        );
    }

    public static void assertMandatoryTogglePropertyConfigs(AbstractTogglePropertyConfigs propertyConfigs, String propertyName) {
        assertNonNullOrThrow(propertyConfigs, propertyName);
        assertPropertyConfigs(
                propertyConfigs,
                propertyName,
                getMandatoryToggleFields(propertyConfigs, propertyName)
        );
    }

    // =================================================================================================================
    // GETTERS
    // =================================================================================================================

    public static List<Field> getMandatoryFields(Object property, String propertyName) {
        return getFields(property, propertyName, Set.of(MandatoryProperty.class));
    }

    public static List<Field> getMandatoryToggleFields(Object property, String propertyName) {
        return getFields(property, propertyName, Set.of(MandatoryProperty.class, MandatoryToggleProperty.class));
    }

    public static List<Field> getMandatoryBasedFields(Object property, String propertyName) {
        return getFields(property, propertyName, Set.of(MandatoryProperty.class, NonMandatoryProperty.class, MandatoryToggleProperty.class));
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================

    private static void assertPropertyConfigs(AbstractPropertyConfigs propertyConfigs, String propertyName, List<Field> fields) {
        assertNonNullOrThrow(propertyConfigs, propertyName);
        fields.forEach(field -> {
            try {
                var jbstProperty = new JbstProperty(propertyName, field, field.get(propertyConfigs));
                ConsoleAsserts.assertNonNullOrThrow(jbstProperty);
                verifyProperty(jbstProperty);
            } catch (IllegalAccessException ex) {
                throw new IllegalArgumentException(ex);
            }
        });
    }

    // TODO [YYL] fixme?
    public static void assertPropertiesConfigs(AbstractPropertiesConfigs propertiesConfigs, List<Field> fields) {
        assertNonNullOrThrow(propertiesConfigs, propertiesConfigs.getPropertyName());
        fields.forEach(field -> {
            try {
                var jbstProperty = new JbstProperty(propertiesConfigs.getPropertyName(), field, field.get(propertiesConfigs));
                ConsoleAsserts.assertNonNullOrThrow(jbstProperty);
                var nestedPropertyClass = jbstProperty.getPropertyValue().getClass();
                if (AbstractPropertiesConfigs.class.isAssignableFrom(nestedPropertyClass)) {
                    ((AbstractPropertiesConfigs) jbstProperty.getPropertyValue()).assertProperties();
                } else if (AbstractPropertyConfigs.class.isAssignableFrom(nestedPropertyClass)) {
                    ((AbstractPropertyConfigs) jbstProperty.getPropertyValue()).assertProperties(jbstProperty.getTreePropertyName());
                } else {
                    verifyProperty(jbstProperty);
                }
            } catch (IllegalAccessException ex) {
                throw new IllegalArgumentException(ex);
            }
        });
    }

    // TODO [YYL} fixme
    @SuppressWarnings({"rawtypes", "DataFlowIssue"})
    public static void verifyProperty(JbstProperty jbstProperty) {
        var property = jbstProperty.getPropertyValue();
        if (jbstProperty.getField().isAnnotationPresent(MandatoryMapProperty.class)) {
            var annotation = jbstProperty.getField().getAnnotation(MandatoryMapProperty.class);
            Class<? extends Enum<?>> keySetClass = annotation.keySetClass();
            var castedProperty = (Map) property;
            var size = (annotation.size() == -1) ? keySetClass.getEnumConstants().length : annotation.size();
            //noinspection unchecked
            assertTrueOrThrow(
                    castedProperty.size() == size,
                    "%s. Options: [%s]. Required: [%s]. Disjunction: [%s]".formatted(
                            jbstProperty.getTreePropertyName(),
                            baseJoiningWildcard(keySetClass),
                            baseJoiningRaw(castedProperty.keySet()),
                            JbstConstants.JColor.RED_TEXT.format(baseJoining(disjunction(castedProperty.keySet(), EnumUtility.setWildcard(keySetClass))))
                    )
            );
        }
        ACTIONS.entrySet().stream()
                .filter(entry -> entry.getKey().apply(property.getClass()))
                .map(Map.Entry::getValue)
                .findFirst()
                .ifPresent(consumer -> consumer.accept(jbstProperty));
    }

    @SuppressWarnings("ConstantValue")
    private static List<Field> getFields(Object property, String propertyName, Set<Class<? extends Annotation>> presentAnnotations) {
        assertNonNullOrThrow(property, propertyName);
        return Stream.of(property.getClass().getDeclaredFields())
                .filter(Objects::nonNull)
                .map(field -> {
                    for (Class<? extends Annotation> annotation : presentAnnotations) {
                        if (field.isAnnotationPresent(annotation)) {
                            field.setAccessible(true);
                            return field;
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .sorted((o1, o2) -> {
                    if ("enabled".equals(o1.getName())) {
                        return -1;
                    } else if ("enabled".equals(o2.getName())) {
                        return 1;
                    }
                    return o1.getName().compareTo(o2.getName());
                })
                .toList();
    }
}
