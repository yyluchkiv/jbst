package jbst.ops.server.domain.servers;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

import static jbst.foundation.utilities.exceptions.TraceUtility.getTrace;
import static jbst.ops.server.domain.servers.FileSystemMetadataRow.PERCENTAGE_REVERSED;
import static org.springframework.util.CollectionUtils.isEmpty;

public record FileSystemMetadata(
        boolean failure,
        List<FileSystemMetadataRow> rows,
        String failureMessage,
        String failureClass,
        String failureTrace
) {
    public static FileSystemMetadata success(List<FileSystemMetadataRow> rows) {
        return new FileSystemMetadata(
                false,
                rows.stream().sorted(PERCENTAGE_REVERSED).toList(),
                "",
                "",
                ""
        );
    }

    public static FileSystemMetadata failure(Throwable failureThrowable) {
        return new FileSystemMetadata(
                true,
                new ArrayList<>(),
                failureThrowable.getMessage(),
                failureThrowable.getClass().toString(),
                getTrace(failureThrowable).value()
        );
    }

    @JsonIgnore
    public boolean isAnyRows() {
        return !this.failure && !isEmpty(this.rows);
    }
}
