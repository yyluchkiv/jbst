package jbst.foundation.domain.properties.utilities;

import jbst.foundation.domain.asserts.Asserts;
import jbst.foundation.domain.asserts.ConsoleAsserts;
import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.properties.annotations.MandatoryMapProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryToggleProperty;
import jbst.foundation.domain.properties.annotations.NonMandatoryProperty;
import jbst.foundation.domain.properties.base.AbstractPropertyConfigs;
import jbst.foundation.domain.properties.base.AbstractTogglePropertyConfigs;
import jbst.foundation.domain.properties.configs.AbstractPropertiesConfigs;
import jbst.foundation.domain.reflections.ReflectionProperty;
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

import static jbst.foundation.utilities.collections.CollectionUtility.baseJoiningRaw;
import static jbst.foundation.utilities.enums.EnumUtility.baseJoining;
import static jbst.foundation.utilities.enums.EnumUtility.baseJoiningWildcard;
import static jbst.foundation.utilities.exceptions.ExceptionConsoleUtility.invalidProperty;
import static org.apache.commons.collections4.SetUtils.disjunction;

@Slf4j
@UtilityClass
public class PropertiesAsserter {
    private static final Map<Function<Class<?>, Boolean>, Consumer<ReflectionProperty>> ACTIONS = new HashMap<>();

    static {
        ACTIONS.put(Date.class::equals, ConsoleAsserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(LocalDate.class::equals, ConsoleAsserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(LocalDateTime.class::equals, ConsoleAsserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(ChronoUnit.class::equals, ConsoleAsserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(TimeUnit.class::equals, ConsoleAsserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(Boolean.class::equals, ConsoleAsserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(Short.class::equals, ConsoleAsserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(Integer.class::equals, ConsoleAsserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(Long.class::equals, ConsoleAsserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(BigInteger.class::equals, ConsoleAsserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(BigDecimal.class::equals, ConsoleAsserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(String.class::equals, ConsoleAsserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(Collection.class::isAssignableFrom, cp -> ConsoleAsserts.assertNonNullNotEmptyOrThrow((Collection<?>) cp.getPropertyValue(), new PropertyId(cp.getPropertyName())));
    }

    // =================================================================================================================
    // Assertions
    // =================================================================================================================

    public static void assertMandatoryPropertiesConfigs(AbstractPropertiesConfigs propertiesConfigs, PropertyId propertyId) {
        ConsoleAsserts.assertNonNullOrThrow(propertiesConfigs, propertyId);
        assertPropertiesConfigs(
                propertiesConfigs,
                propertyId,
                getMandatoryFields(propertiesConfigs, propertyId)
        );
    }

    public static void assertMandatoryTogglePropertiesConfigs(AbstractPropertiesConfigs propertiesConfigs, PropertyId propertyId) {
        ConsoleAsserts.assertNonNullOrThrow(propertiesConfigs, propertyId);
        assertPropertiesConfigs(
                propertiesConfigs,
                propertyId,
                getMandatoryToggleFields(propertiesConfigs, propertyId)
        );
    }

    public static void assertMandatoryPropertyConfigs(AbstractPropertyConfigs propertyConfigs, PropertyId propertyId) {
        ConsoleAsserts.assertNonNullOrThrow(propertyConfigs, propertyId);
        assertPropertyConfigs(
                propertyConfigs,
                propertyId,
                getMandatoryFields(propertyConfigs, propertyId)
        );
    }

    public static void assertMandatoryTogglePropertyConfigs(AbstractTogglePropertyConfigs propertyConfigs, PropertyId propertyId) {
        ConsoleAsserts.assertNonNullOrThrow(propertyConfigs, propertyId);
        assertPropertyConfigs(
                propertyConfigs,
                propertyId,
                getMandatoryToggleFields(propertyConfigs, propertyId)
        );
    }

    // =================================================================================================================
    // GETTERS
    // =================================================================================================================

    public static List<Field> getMandatoryFields(Object property, PropertyId propertyId) {
        return getFields(property, propertyId, Set.of(MandatoryProperty.class));
    }

    public static List<Field> getMandatoryToggleFields(Object property, PropertyId propertyId) {
        return getFields(property, propertyId, Set.of(MandatoryProperty.class, MandatoryToggleProperty.class));
    }

    public static List<Field> getMandatoryBasedFields(Object property, PropertyId propertyId) {
        return getFields(property, propertyId, Set.of(MandatoryProperty.class, NonMandatoryProperty.class, MandatoryToggleProperty.class));
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================

    private static void assertPropertyConfigs(AbstractPropertyConfigs propertyConfigs, PropertyId propertyId, List<Field> fields) {
        ConsoleAsserts.assertNonNullOrThrow(propertyConfigs, propertyId);
        fields.forEach(field -> {
            try {
                var rf = new ReflectionProperty(propertyId, field, field.get(propertyConfigs));
                ConsoleAsserts.assertNonNullPropertyOrThrow(rf);
                verifyProperty(rf);
            } catch (IllegalAccessException ex) {
                throw new IllegalArgumentException(ex);
            }
        });
    }

    private static void assertPropertiesConfigs(AbstractPropertiesConfigs propertiesConfigs, PropertyId propertyId, List<Field> fields) {
        ConsoleAsserts.assertNonNullOrThrow(propertiesConfigs, propertyId);
        fields.forEach(field -> {
            try {
                var rf = new ReflectionProperty(propertyId, field, field.get(propertiesConfigs));
                ConsoleAsserts.assertNonNullPropertyOrThrow(rf);
                var nestedPropertyClass = rf.getPropertyValue().getClass();
                if (AbstractPropertiesConfigs.class.isAssignableFrom(nestedPropertyClass)) {
                    ((AbstractPropertiesConfigs) rf.getPropertyValue()).assertProperties();
                } else if (AbstractPropertyConfigs.class.isAssignableFrom(nestedPropertyClass)) {
                    ((AbstractPropertyConfigs) rf.getPropertyValue()).assertProperties(rf.getTreePropertyId());
                } else {
                    verifyProperty(rf);
                }
            } catch (IllegalAccessException ex) {
                throw new IllegalArgumentException(ex);
            }
        });
    }

    @SuppressWarnings({"rawtypes", "DataFlowIssue"})
    private static void verifyProperty(ReflectionProperty rf) {
        var property = rf.getPropertyValue();
        if (rf.getField().isAnnotationPresent(MandatoryMapProperty.class)) {
            var annotation = rf.getField().getAnnotation(MandatoryMapProperty.class);
            Class<? extends Enum<?>> keySetClass = annotation.keySetClass();
            var castedProperty = (Map) property;
            var size = (annotation.size() == -1) ? keySetClass.getEnumConstants().length : annotation.size();
            //noinspection unchecked
            Asserts.assertTrueOrThrow(
                    castedProperty.size() == size,
                    "%s. Options: \"[%s]\". Required: \"[%s]\". Disjunction: \"[%s]\"".formatted(
                            invalidProperty(rf.getTreePropertyId()),
                            baseJoiningWildcard(keySetClass),
                            baseJoiningRaw(castedProperty.keySet()),
                            ConsoleAsserts.RED_TEXT.format(baseJoining(disjunction(castedProperty.keySet(), EnumUtility.setWildcard(keySetClass))))
                    )
            );
        }
        ACTIONS.entrySet().stream()
                .filter(entry -> entry.getKey().apply(property.getClass()))
                .map(Map.Entry::getValue)
                .findFirst()
                .ifPresent(consumer -> consumer.accept(rf));
    }

    @SuppressWarnings("ConstantValue")
    private static List<Field> getFields(Object property, PropertyId propertyId, Set<Class<? extends Annotation>> presentAnnotations) {
        ConsoleAsserts.assertNonNullOrThrow(property, propertyId);
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
