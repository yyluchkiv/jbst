package jbst.foundation.utilities.reflections;

import jbst.foundation.domain.tuples.Tuple2;
import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.uncapitalize;

@SuppressWarnings({"unchecked", "unused"})
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

    @SuppressWarnings("ConstantValue")
    public static List<Field> getFields(Object property, Set<Class<? extends Annotation>> presentAnnotations) {
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
