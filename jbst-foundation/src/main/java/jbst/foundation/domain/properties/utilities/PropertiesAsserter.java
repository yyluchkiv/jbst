package jbst.foundation.domain.properties.utilities;

import jbst.foundation.domain.asserts.ConsoleAsserts;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryToggleProperty;
import jbst.foundation.domain.properties.annotations.NonMandatoryProperty;
import jbst.foundation.domain.properties.base.AbstractPropertyConfigs;
import jbst.foundation.domain.reflections.JbstProperty;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import static jbst.foundation.domain.asserts.ConsoleAsserts.assertNonNullOrThrow;
import static jbst.foundation.utilities.reflections.ReflectionUtility.getFields;

@Slf4j
@UtilityClass
public class PropertiesAsserter {
    // =================================================================================================================
    // GETTERS
    // =================================================================================================================

    public static List<Field> getMandatoryFields(Object property, String propertyName) {
        assertNonNullOrThrow(property, propertyName);
        return getFields(property, Set.of(MandatoryProperty.class));
    }

    public static List<Field> getMandatoryToggleFields(Object property, String propertyName) {
        assertNonNullOrThrow(property, propertyName);
        return getFields(property, Set.of(MandatoryProperty.class, MandatoryToggleProperty.class));
    }

    public static List<Field> getMandatoryBasedFields(Object property, String propertyName) {
        assertNonNullOrThrow(property, propertyName);
        return getFields(property, Set.of(MandatoryProperty.class, NonMandatoryProperty.class, MandatoryToggleProperty.class));
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
}
