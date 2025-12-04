package jbst.foundation.domain.collections;

import jbst.foundation.domain.constants.JbstConstants;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.requireNonNull;
import static jbst.foundation.domain.asserts.JbstAsserts.assertTrueOrThrow;
import static org.springframework.util.CollectionUtils.*;

@UtilityClass
public class JbstCollections {

    public <E> ConcurrentHashMap.KeySetView<E, Boolean> toConcurrentSet(Collection<E> list) {
        ConcurrentHashMap.KeySetView<E, Boolean> set = ConcurrentHashMap.newKeySet();
        set.addAll(list);
        return set;
    }

    public static <T> List<T> mutableSingletonList(T object) {
        List<T> list = new ArrayList<>(1);
        list.add(object);
        return list;
    }

    public static <T> Set<T> mutableSingletonSet(T object) {
        Set<T> list = new HashSet<>(1);
        list.add(object);
        return list;
    }

    public static <K, V> Map<K, V> mutableSingletonMap(K key, V value) {
        Map<K, V> map = new HashMap<>(1);
        map.put(key, value);
        return map;
    }

    public static <T> Queue<T> emptyQueue() {
        return new LinkedList<>();
    }

    public static <T> List<T> union(List<T> one, List<T> second) {
        List<T> list = new ArrayList<>(one.size() + second.size());
        list.addAll(one);
        list.addAll(second);
        return list;
    }

    public static <T> Set<T> union(Set<T> one, Set<T> second) {
        Set<T> list = new HashSet<>(one.size() + second.size());
        list.addAll(one);
        list.addAll(second);
        return list;
    }

    public static <T> T safeFirstElement(List<T> list) {
        return requireNonNull(firstElement(list));
    }

    public static <T> T safeFirstElement(Set<T> set) {
        return requireNonNull(firstElement(set));
    }

    public static <T> T safeLastElement(List<T> list) {
        return requireNonNull(lastElement(list));
    }

    public static <T> T safeLastElement(Set<T> set) {
        return requireNonNull(lastElement(set));
    }

    public static <T> List<T> getFirstNElements(List<T> list, long n) {
        assertTrueOrThrow(n >= 0, "Elements quantity can't be negative");
        return list.stream().limit(n).collect(Collectors.toList());
    }

    public static <T> List<T> getLastNElements(List<T> list, long n) {
        assertTrueOrThrow(n >= 0, "Elements quantity can't be negative");
        var remainingCount = list.size() - n;
        var remainingCountOrZero = (remainingCount >= 0) ? remainingCount : 0;
        return list.stream().skip(remainingCountOrZero).collect(Collectors.toList());
    }

    public static BigDecimal maxOrZero(List<BigDecimal> values) {
        return !isEmpty(values) ? Collections.max(values) : ZERO;
    }

    public static BigDecimal minOrZero(List<BigDecimal> values) {
        return !isEmpty(values) ? Collections.min(values) : ZERO;
    }

    public static String baseJoiningRaw(Collection<?> collection) {
        return collection.stream().map(Object::toString).sorted().collect(Collectors.joining(JbstConstants.Symbols.COLLECTORS_COMMA_SPACE));
    }
}
