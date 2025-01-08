package jbst.ops.server.domain.keywords;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Operation {
    FS_TABLES("file systems metadata");

    private final String readableValue;

    public String getMessage() {
        return String.format(
                ":information_source: Executed operation response: *%s*",
                this.getReadableValue()
        );
    }
}
