package jbst.foundation.domain.properties.utilities;

import jbst.foundation.domain.asserts.ConsoleAsserts;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryToggleProperty;
import jbst.foundation.domain.properties.annotations.NonMandatoryProperty;
import jbst.foundation.domain.properties.base.AbstractPropertyConfigs;
import jbst.foundation.domain.properties.base.AbstractTogglePropertyConfigs;
import jbst.foundation.domain.properties.configs.AbstractPropertiesConfigs;
import jbst.foundation.domain.reflections.JbstProperty;
import jbst.foundation.utilities.reflections.ReflectionUtility;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static jbst.foundation.domain.asserts.ConsoleAsserts.assertNonNullOrThrow;

@Slf4j
@UtilityClass
public class PropertiesAsserter {
    private static final Map<Function<Class<?>, Boolean>, Consumer<JbstProperty>> ACTIONS = new HashMap<>();

//    static {
//        ACTIONS.put(Date.class::equals, ConsoleAsserts::assertNonNullOrThrow);
//        ACTIONS.put(LocalDate.class::equals, ConsoleAsserts::assertNonNullOrThrow);
//        ACTIONS.put(LocalDateTime.class::equals, ConsoleAsserts::assertNonNullOrThrow);
//        ACTIONS.put(ChronoUnit.class::equals, ConsoleAsserts::assertNonNullOrThrow);
//        ACTIONS.put(TimeUnit.class::equals, ConsoleAsserts::assertNonNullOrThrow);
//        ACTIONS.put(Boolean.class::equals, ConsoleAsserts::assertNonNullOrThrow);
//        ACTIONS.put(Short.class::equals, ConsoleAsserts::assertNonNullOrThrow);
//        ACTIONS.put(Integer.class::equals, ConsoleAsserts::assertNonNullOrThrow);
//        ACTIONS.put(Long.class::equals, ConsoleAsserts::assertNonNullOrThrow);
//        ACTIONS.put(BigInteger.class::equals, ConsoleAsserts::assertNonNullOrThrow);
//        ACTIONS.put(BigDecimal.class::equals, ConsoleAsserts::assertNonNullOrThrow);
//        ACTIONS.put(String.class::equals, ConsoleAsserts::assertNonNullOrThrow);
//        ACTIONS.put(Collection.class::isAssignableFrom, property -> {
//            var collection = (Collection<?>) property.getPropertyValue();
//            ConsoleAsserts.assertNonNullOrThrow(collection, property.getPropertyName());
//            ConsoleAsserts.assertNonEmptyOrThrow(collection, property.getPropertyName());
//        });
//    }

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
        assertNonNullOrThrow(property, propertyName);
        return ReflectionUtility.getFields(property, propertyName, Set.of(MandatoryProperty.class));
    }

    public static List<Field> getMandatoryToggleFields(Object property, String propertyName) {
        assertNonNullOrThrow(property, propertyName);
        return ReflectionUtility.getFields(property, propertyName, Set.of(MandatoryProperty.class, MandatoryToggleProperty.class));
    }

    public static List<Field> getMandatoryBasedFields(Object property, String propertyName) {
        assertNonNullOrThrow(property, propertyName);
        return ReflectionUtility.getFields(property, propertyName, Set.of(MandatoryProperty.class, NonMandatoryProperty.class, MandatoryToggleProperty.class));
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
                jbstProperty.verify();
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
                    jbstProperty.verify();
                }
            } catch (IllegalAccessException ex) {
                throw new IllegalArgumentException(ex);
            }
        });
    }
}
