package jbst.foundation.domain.plurals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toUnmodifiableMap;

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public abstract class PluralsSet<T extends Plurable<ID>, ID> {
    protected final Set<T> values;
    protected final Map<ID, T> mappedValues;

    protected PluralsSet(Set<T> values) {
        this.values = unmodifiableSet(values);
        this.mappedValues = values.stream().collect(toUnmodifiableMap(
                Plurable::getId,
                entry -> entry,
                (existing, replacement) -> existing
        ));
    }

    @JsonIgnore
    public final Set<ID> getIds() {
        return this.values.stream().map(Plurable::getId).collect(Collectors.toSet());
    }

    @SuppressWarnings("unused")
    @JsonIgnore
    public final Set<ID> getUniqueIds() {
        return this.mappedValues.keySet();
    }

    public final T getOneOrNull(ID id) {
        return this.mappedValues.get(id);
    }
}
