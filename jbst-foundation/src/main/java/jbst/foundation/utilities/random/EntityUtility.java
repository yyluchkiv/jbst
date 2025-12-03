package jbst.foundation.utilities.random;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.geo.GeoLocation;
import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.domain.http.requests.UserAgentDetails;
import jbst.foundation.domain.http.requests.UserRequestMetadata;
import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Objects.nonNull;
import static jbst.foundation.utilities.collections.JbstCollections.emptyQueue;

@SuppressWarnings({ "unchecked" })
@UtilityClass
public class EntityUtility {

    private static final Map<Class<?>, Function<Class<?>, Object>> CONSTRUCTORS_RULES = new HashMap<>();

    private static final Deque<Function<Class<?>, Optional<?>>> RANDOM_TYPES_RULES = new LinkedList<>();

    static {
        addConstructorRule(BigDecimal.class, clazz -> RandomUtility.randomBigDecimal());
        addConstructorRule(BigInteger.class, clazz -> RandomUtility.randomBigInteger());
        addConstructorRule(Long.class, clazz -> RandomUtility.randomLong());
        addConstructorRule(Integer.class, clazz -> RandomUtility.randomInteger());
        addConstructorRule(Double.class, clazz -> RandomUtility.randomDouble());

        addConstructorRule(ZoneId.class, clazz -> RandomUtility.randomZoneId());
        addConstructorRule(TimeZone.class, clazz -> RandomUtility.randomTimeZone());
        addConstructorRule(Username.class, clazz -> Username.random());
        addConstructorRule(Password.class, clazz -> Password.random());
        addConstructorRule(Email.class, clazz -> Email.random());

        addConstructorRule(IPAddress.class, clazz -> IPAddress.random());
        addConstructorRule(GeoLocation.class, clazz -> GeoLocation.random());
        addConstructorRule(UserAgentDetails.class, clazz -> UserAgentDetails.random());
        addConstructorRule(UserRequestMetadata.class, clazz -> UserRequestMetadata.random());

        addClassRule(parameterClass -> {
                    var isNotPrimitiveOrWrapper = !parameterClass.isPrimitive() && !RandomUtility.containsPrimitiveWrapper(parameterClass);
                    var isNotEnum = !Enum.class.isAssignableFrom(parameterClass);
                    var isNotAnnotation = !Annotation.class.isAssignableFrom(parameterClass);
                    var isNotInterface = !parameterClass.isInterface();
                    return isNotPrimitiveOrWrapper && isNotEnum && isNotAnnotation && isNotInterface;
                },
                EntityUtility::entity
        );

        addClassRule(Class::isEnum, RandomUtility::randomEnumWildcard);

        addClassRule(List.class::equals, parameterClass -> emptyList());
        addClassRule(Set.class::equals, parameterClass -> emptySet());
        addClassRule(Queue.class::equals, parameterClass -> emptyQueue());

        addClassRule(Date.class::equals, parameterClass -> RandomUtility.randomDate());
        addClassRule(LocalDate.class::equals, parameterClass -> RandomUtility.randomLocalDate());
        addClassRule(LocalDateTime.class::equals, parameterClass -> RandomUtility.randomLocalDateTime());

        addClassRule(BigDecimal.class::equals, parameterClass -> RandomUtility.randomBigDecimal());

        addClassRule(BigInteger.class::equals, parameterClass -> RandomUtility.randomBigInteger());

        addClassRule(Double.class::equals, parameterClass -> RandomUtility.randomDouble());
        addClassRule(double.class::equals, parameterClass -> RandomUtility.randomDouble());

        addClassRule(Long.class::equals, parameterClass -> RandomUtility.randomLong());
        addClassRule(long.class::equals, parameterClass -> RandomUtility.randomLong());

        addClassRule(Integer.class::equals, parameterClass -> RandomUtility.randomInteger());
        addClassRule(int.class::equals, parameterClass -> RandomUtility.randomInteger());

        addClassRule(String.class::equals, parameterClass -> RandomUtility.randomString());

        addClassRule(Short.class::equals, parameterClass -> RandomUtility.randomShort());
        addClassRule(short.class::equals, parameterClass -> RandomUtility.randomShort());

        addClassRule(Boolean.class::equals, parameterClass -> RandomUtility.randomBoolean());
        addClassRule(boolean.class::equals, parameterClass -> RandomUtility.randomBoolean());

        addClassRule(ZoneId.class::equals, parameterClass -> RandomUtility.randomZoneId());
        addClassRule(TimeZone.class::equals, parameterClass -> RandomUtility.randomTimeZone());
        addClassRule(Username.class::equals, parameterClass -> Username.random());
        addClassRule(Password.class::equals, parameterClass -> Password.random());
        addClassRule(Email.class::equals, parameterClass -> Email.random());
        addClassRule(TimeUnit.class::equals, parameterClass -> RandomUtility.randomTimeUnit());
        addClassRule(ChronoUnit.class::equals, parameterClass -> RandomUtility.randomChronoUnit());

        addClassRule(IPAddress.class::equals, parameterClass -> IPAddress.random());
        addClassRule(GeoLocation.class::equals, parameterClass -> GeoLocation.random());
        addClassRule(UserAgentDetails.class::equals, parameterClass -> UserAgentDetails.random());
        addClassRule(UserRequestMetadata.class::equals, parameterClass -> UserRequestMetadata.random());
    }

    public static void addConstructorRule(Class<?> constructorClass, Function<Class<?>, Object> constructionFnc) {
        CONSTRUCTORS_RULES.put(constructorClass, constructionFnc);
    }

    public static void addClassRule(Predicate<Class<?>> newPredicate, Function<Class<?>, ?> newFunction) {
        RANDOM_TYPES_RULES.addFirst(definedRule -> {
            if (newPredicate.test(definedRule)) {
                return Optional.of(newFunction.apply(definedRule));
            } else {
                return Optional.empty();
            }
        });
    }

    // TASKS: add random method to classes that uses this method
    public static <T> T entity(Class<? extends T> type) {
        try {
            var clazzFunction = CONSTRUCTORS_RULES.get(type);
            if (nonNull(clazzFunction)) {
                return (T) clazzFunction.apply(type);
            } else {
                return createWithDefaultConstructor(type);
            }
        } catch (ReflectiveOperationException ex1) {
            try {
                return createNoDefaultConstructor(type);
            } catch (ReflectiveOperationException ex2) {
                try {
                    return createPrivateDataLombokBasedConstructor(type);
                } catch (ReflectiveOperationException ex3) {
                    throw new IllegalArgumentException("Please add entity construction rules or extend functionality. Class: `" + type.getCanonicalName() + "`");
                }
            }
        }
    }

    public static <T> List<T> list(Class<? extends T> type, int size) {
        return IntStream.range(0, size).mapToObj(i -> entity(type)).collect(Collectors.toList());
    }

    public static <T> List<T> list345(Class<? extends T> type) {
        return list(type, RandomUtility.randomIntegerGreaterThanZeroByBounds(2, 5));
    }

    public static <T> Set<T> set(Class<? extends T> type, int size) {
        return IntStream.range(0, size).mapToObj(i -> entity(type)).collect(Collectors.toSet());
    }

    public static <T> Set<T> set345(Class<? extends T> type) {
        return set(type, RandomUtility.randomIntegerGreaterThanZeroByBounds(2, 5));
    }

    // =================================================================================================================
    // PROTECTED METHODS
    // =================================================================================================================
    public static <T> void setObjectFields(T instance) throws ReflectiveOperationException {
        if (instance == null) {
            throw new ReflectiveOperationException("Cannot invoke setter. Instance is null");
        }
        var setters = Stream.of(instance.getClass().getMethods())
                .filter(method -> {
                    Class<?>[] params = method.getParameterTypes();
                    return method.getName().startsWith("set") && params.length > 0;
                }).toList();

        for (Method setter : setters) {
            try {
                var setterClass = setter.getParameterTypes()[0];
                setter.invoke(instance, getRandomValueByClass(setterClass));
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                var message = "Cannot invoke setter. Unexpected setter signature";
                throw new ReflectiveOperationException(message);
            }
        }
    }

    public static Object getRandomValueByClass(Class<?> rndClass) {
        Optional<? extends Optional<?>> matches = RANDOM_TYPES_RULES.stream()
                .map(item -> item.apply(rndClass))
                .filter(Optional::isPresent)
                .findFirst();
        if (matches.isPresent()) {
            Optional<?> randomValueOpt = matches.get();
            if (randomValueOpt.isPresent()) {
                return randomValueOpt.get();
            }
        }
        return null;
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private static <T> T createWithDefaultConstructor(Class<? extends T> type) throws ReflectiveOperationException {
        T instance = type.getDeclaredConstructor().newInstance();
        setObjectFields(instance);
        return instance;
    }

    private static <T> T createNoDefaultConstructor(Class<? extends T> type) throws ReflectiveOperationException {
        var constructors = type.getConstructors();
        if (constructors.length == 0) {
            throw new ReflectiveOperationException("There is no constructors to initiate instance of type: " + type);
        }
        var constructor = constructors[0];
        var args = Stream.of(constructor.getParameterTypes())
                .map(EntityUtility::getRandomValueByClass)
                .toArray();
        var instance = (T) constructor.newInstance(args);
        setObjectFields(instance);
        return instance;
    }

    private static <T> T createPrivateDataLombokBasedConstructor(Class<? extends T> type) throws ReflectiveOperationException {
        var constructor = type.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        var args = Stream.of(constructor.getParameterTypes())
                .map(EntityUtility::getRandomValueByClass)
                .toArray();
        // ignored setObjectFields() -> @Data constructor construct immutable object
        return (T) constructor.newInstance(args);
    }
}
