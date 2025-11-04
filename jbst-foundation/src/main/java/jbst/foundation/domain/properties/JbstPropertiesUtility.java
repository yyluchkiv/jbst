package jbst.foundation.domain.properties;

import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatoryOnToggleEnabled;
import jbst.foundation.domain.properties.annotations.NonMandatoryProperty;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import static jbst.foundation.domain.asserts.ConsoleAsserts.assertNonNullOrThrow;
import static jbst.foundation.utilities.reflections.ReflectionUtility.getFields;

@UtilityClass
public class JbstPropertiesUtility {

    public static List<Field> getMandatoryFields(Object property, String propertyName) {
        assertNonNullOrThrow(property, propertyName);
        return getFields(property, Set.of(JbstPropertyMandatory.class));
    }

    public static List<Field> getMandatoryToggleFields(Object property, String propertyName) {
        assertNonNullOrThrow(property, propertyName);
        return getFields(property, Set.of(JbstPropertyMandatory.class, JbstPropertyMandatoryOnToggleEnabled.class));
    }

    public static List<Field> getMandatoryBasedFields(Object property, String propertyName) {
        assertNonNullOrThrow(property, propertyName);
        return getFields(property, Set.of(NonMandatoryProperty.class, JbstPropertyMandatory.class, JbstPropertyMandatoryOnToggleEnabled.class));
    }
}
