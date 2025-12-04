package jbst.foundation.domain.states;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// Lombok
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class ClassicStateGroupedMappings {
    private final Map<ClassicState, Long> values;
    private final boolean empty;

    public static ClassicStateGroupedMappings hardcoded() {
        return new ClassicStateGroupedMappings(
                List.of(
                        ClassicState.CREATED,
                        ClassicState.ACTIVE
                )
        );
    }

    public ClassicStateGroupedMappings(@NotNull List<ClassicState> values) {
        this.values = values.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey(ClassicState.ORDINAL_COMPARATOR))
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (oldValue, newValue) -> oldValue,
                                LinkedHashMap::new
                        )
                );
        this.empty = CollectionUtils.isEmpty(values);
    }
}
