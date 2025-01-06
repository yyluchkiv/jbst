package jbst.ops.server.domain.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum SupportedFormat {
    ZIP("zip");

    private final String value;

    public static boolean contains(String format) {
        return Stream.of(SupportedFormat.values()).anyMatch(sf -> sf.getValue().equals(format));
    }
}
