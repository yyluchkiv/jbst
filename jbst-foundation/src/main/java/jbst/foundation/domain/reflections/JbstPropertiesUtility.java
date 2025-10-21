package jbst.foundation.domain.reflections;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.properties.base.SchedulerConfiguration;
import jbst.foundation.domain.properties.base.TimeAmount;
import lombok.experimental.UtilityClass;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@UtilityClass
public class JbstPropertiesUtility {

    @SuppressWarnings({"ConstantValue", "unused"})
    public static List<JbstPropertyEdge> getNotNullPropertiesRecursively(Object object, String propertyName) {
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

        List<JbstPropertyEdge> traversedProperties = new ArrayList<>();
        var properties = getNotNullProperties(object, propertyName);
        properties.forEach(property -> {
            if (breakoutClassesPredicate.test(property.getValue())) {
                traversedProperties.add(property);
            } else {
                traversedProperties.addAll(getNotNullPropertiesRecursively(property.getValue(), property.getName()));
            }
        });
        return traversedProperties;
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private static List<JbstPropertyEdge> getNotNullProperties(Object object, String propertyName) {
        return Stream.of(object.getClass().getDeclaredFields())
                .map(field -> {
                    try {
                        var nestedProperty = field.get(object);
                        if (isNull(nestedProperty)) {
                            return null;
                        } else {
//                            return new JbstProperty(propertyName, field, nestedProperty);
                            return new JbstPropertyEdge(null, field, nestedProperty);
                        }
                    } catch (IllegalAccessException | RuntimeException ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
