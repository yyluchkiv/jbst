package jbst.foundation.utilities.reflections;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.base.PropertyName;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.properties.base.SchedulerConfiguration;
import jbst.foundation.domain.properties.base.TimeAmount;
import jbst.foundation.domain.reflections.JbstProperty;
import jbst.foundation.domain.reflections.ReflectionProperty;
import jbst.foundation.domain.tuples.Tuple2;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.uncapitalize;

@SuppressWarnings("unchecked")
@UtilityClass
public class ReflectionUtility {

    public static <T, S> void setPrivateField(T object, String nameField, S valueField) throws NoSuchFieldException, IllegalAccessException {
        var field = object.getClass().getDeclaredField(nameField);
        field.setAccessible(true);
        field.set(object, valueField);
    }

    public static <T, S> void setPrivateFieldOfSuperClass(T object, String nameField, S valueField, int numberOfSuperClass) throws NoSuchFieldException, IllegalAccessException {
        Class<?> expectedClass = object.getClass();
        for (var i = 0 ; i < numberOfSuperClass; i++) {
            expectedClass = expectedClass.getSuperclass();
        }
        var field = expectedClass.getDeclaredField(nameField);
        field.setAccessible(true);
        field.set(object, valueField);
    }

    public static <T, S> S getPrivateField(T object, String nameField) throws NoSuchFieldException, IllegalAccessException {
        var field = object.getClass().getDeclaredField(nameField);
        field.setAccessible(true);
        return (S) field.get(object);
    }

    public static <T, S> S getPrivateFieldOfSuperClass(T object, String nameField, int numberOfSuperClass) throws NoSuchFieldException, IllegalAccessException {
        Class<?> expectedClass = object.getClass();
        for (var i = 0 ; i < numberOfSuperClass; i++) {
            expectedClass = expectedClass.getSuperclass();
        }
        var field = expectedClass.getDeclaredField(nameField);
        field.setAccessible(true);
        return (S) field.get(object);
    }

    public static void objectFieldHook(Class<?> type, Object hook, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        var field = type.getDeclaredField(fieldName);
        field.setAccessible(true);
        var modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, hook);
    }

    public static void assertFieldsNotNull(Object object) {
        var declaredFields = object.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(object);
            } catch (IllegalAccessException ex) {
                // ignored
            }
            assert nonNull(value) : "Field " + field.getName() + " is null";
        }
    }

    public static <T> List<String> getAllNullFields(T object) {
        var declaredFields = object.getClass().getDeclaredFields();
        final List<String> nullFields = new ArrayList<>();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            try {
                final Object o = field.get(object);
                if (isNull(o)) {
                    nullFields.add(field.getName());
                }
            } catch (IllegalAccessException ex) {
                // ignored
            }
        }
        return nullFields;
    }

    public static List<Method> getGetters(Object object) {
        return Stream.of(object.getClass().getMethods())
                .filter(method -> method.getParameterTypes().length == 0)
                .filter(method -> {
                    var methodName = method.getName();
                    return methodName.startsWith("get") || methodName.startsWith("is");
                })
                .filter(method -> {
                    var methodName = method.getName();
                    return !"getClass".equals(methodName) && !"getDeclaringClass".equals(methodName);
                })
                .collect(Collectors.toList());
    }

    public static List<ReflectionProperty> getNotNullPropertiesRecursively(Object object, PropertyId propertyId) {
        Predicate<Object> breakoutClassesPredicate = breakoutObj -> {
            var clazz = breakoutObj.getClass();
            var isArray = clazz.isArray();
            var isMap = Map.class.isAssignableFrom(clazz);
            var isSet = Set.class.isAssignableFrom(clazz);
            return isArray || isMap || isSet ||
                    Username.class.equals(clazz) ||
                    Password.class.equals(clazz) ||
                    ZoneId.class.isAssignableFrom(clazz) ||
                    ChronoUnit.class.equals(clazz) ||
                    TimeUnit.class.equals(clazz) ||
                    TimeAmount.class.equals(clazz) ||
                    SchedulerConfiguration.class.equals(clazz) ||
                    String.class.equals(clazz) ||
                    boolean.class.equals(clazz) || Boolean.class.equals(clazz) ||
                    short.class.equals(clazz) || Short.class.equals(clazz) ||
                    int.class.equals(clazz) || Integer.class.equals(clazz) ||
                    long.class.equals(clazz) || Long.class.equals(clazz);
        };

        List<ReflectionProperty> traversedProperties = new ArrayList<>();
        var properties = getNotNullProperties(object, propertyId);
        properties.forEach(property -> {
            if (breakoutClassesPredicate.test(property.getPropertyValue())) {
                traversedProperties.add(property);
            } else {
                traversedProperties.addAll(getNotNullPropertiesRecursively(property.getPropertyValue(), property.getTreePropertyId()));
            }
        });
        return traversedProperties;
    }

    public static List<ReflectionProperty> getNotNullProperties(Object object, PropertyId propertyId) {
        return Stream.of(object.getClass().getDeclaredFields())
                .map(field -> {
                    try {
                        var nestedProperty = field.get(object);
                        if (isNull(nestedProperty)) {
                            return null;
                        } else {
                            return new ReflectionProperty(propertyId, field, nestedProperty);
                        }
                    } catch (IllegalAccessException | RuntimeException ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Deprecated
    public static List<ReflectionProperty> getProperties(Object property, PropertyId propertyId, List<Field> fields) {
        return fields.stream()
                .map(field -> {
                    try {
                        return new ReflectionProperty(propertyId, field, field.get(property));
                    } catch (IllegalAccessException | RuntimeException ex) {
                        return new ReflectionProperty(propertyId, field, null);
                    }
                })
                .collect(Collectors.toList());
    }

    public static List<JbstProperty> getProperties(Object property, PropertyName propertyName, List<Field> fields) {
        return fields.stream()
                .map(field -> {
                    try {
                        return new JbstProperty(propertyName, field, field.get(property));
                    } catch (IllegalAccessException | RuntimeException ex) {
                        return new JbstProperty(propertyName, field, null);
                    }
                })
                .collect(Collectors.toList());
    }

    public static Object getFieldValueOrNull(Object object, Field field, List<Method> getters) {
        var getterOpt = getters.stream()
                .filter(method -> method.getName().equalsIgnoreCase("get" + field.getName()))
                .reduce((a, b) -> {
                    throw new IllegalArgumentException("Reflection utility. Multiple getters: " + a + ", " + b);
                });
        if (getterOpt.isPresent()) {
            try {
                var value = getterOpt.get().invoke(object);
                return isNull(value) ? null : value;
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Reflection utility. Missing getter, field: " + field.getName());
        }
    }

    public static Tuple2<Field, Object> getFieldTuple2(Object object, Field field, List<Method> getters) {
        var fieldValueOrNull = getFieldValueOrNull(object, field, getters);
        return new Tuple2<>(field, fieldValueOrNull);
    }

    public static String getPropertyName(Method method) {
        // replace get/is at beginning
        return uncapitalize(
                method.getName()
                        .replaceAll("^get", "")
                        .replaceAll("^is", "")
        );
    }
}
