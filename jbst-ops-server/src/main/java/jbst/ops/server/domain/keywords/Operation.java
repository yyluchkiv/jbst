package jbst.ops.server.domain.keywords;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Operation {
    FS_TABLES("file systems metadata");

    private final String readableValue;
}
