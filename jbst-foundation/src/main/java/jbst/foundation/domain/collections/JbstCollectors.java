package jbst.foundation.domain.collections;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collector;

import static java.util.stream.Collectors.toCollection;

@UtilityClass
public class JbstCollectors {

    public static <T> Collector<T, ?, T> toSingleton() {
        return java.util.stream.Collectors.collectingAndThen(
                java.util.stream.Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException("Collector toSingleton() expects one element");
                    }
                    return list.getFirst();
                }
        );
    }

    public static <T> Collector<T, ?, ConcurrentHashMap.KeySetView<T, Boolean>> toConcurrentKeySet() {
        return toCollection(ConcurrentHashMap::newKeySet);
    }
}
