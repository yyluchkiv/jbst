package jbst.foundation.domain.plurals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableList;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public abstract class Plurals<T extends Plurable<ID>, ID> {
    protected final List<T> values;
    protected final Map<ID, T> mappedValues;

    protected Plurals(List<T> values) {
        this.values = unmodifiableList(values);
        this.mappedValues = values.stream().collect(Collectors.toUnmodifiableMap(Plurable::getId, entry -> entry));
    }

    public final T getOne(ID id) {
        return this.mappedValues.get(id);
    }

    @JsonIgnore
    public final List<ID> getIds() {
        return this.values.stream().map(Plurable::getId).toList();
    }

    @JsonIgnore
    public final Set<ID> getUniqueIds() {
        return this.mappedValues.keySet();
    }
}
