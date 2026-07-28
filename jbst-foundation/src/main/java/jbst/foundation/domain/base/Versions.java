package jbst.foundation.domain.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public record Versions(List<Version> values) {

    public static Versions of(Set<Version> versions) {
        return new Versions(
                new ArrayList<>(versions)
        );
    }

    public static Versions fixed() {
        return new Versions(
                List.of(Version.VERSION_1_3, Version.VERSION_1_4, Version.VERSION_1_5)
        );
    }

    public static Versions empty() {
        return new Versions(List.of());
    }

    public Versions(List<Version> values) {
        this.values = values.stream().sorted(Version.NATURAL_ORDER).toList();
    }
}
