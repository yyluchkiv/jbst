package jbst.server.ops.domain.servers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jbst.foundation.domain.base.ServerName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static jbst.foundation.domain.strings.JbstTraces.getTrace;
import static jbst.foundation.domain.numbers.JbstNumbers.is;
import static jbst.server.ops.domain.servers.ServerFileSystemMetadata.FileSystemMetadataRow.PERCENTAGE_REVERSED;
import static org.springframework.util.CollectionUtils.isEmpty;

public record ServerFileSystemMetadata(
        boolean failure,
        List<FileSystemMetadataRow> rows,
        String failureMessage,
        String failureClass,
        String failureTrace
) {
    // Lombok
    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class FileSystemMetadataRow {
        public static final Comparator<FileSystemMetadataRow> PERCENTAGE_REVERSED = Comparator.comparing(FileSystemMetadataRow::getUsePercentageValue).reversed();

        private final ServerName serverName;
        private final String updatedAt;
        private final String fs;
        private final String size;
        private final String used;
        private final String available;
        private final String usePercentage;
        private final BigDecimal usePercentageValue;
        private final String mountedOn;

        public FileSystemMetadataRow(ServerName serverName, String updatedAt, String line) {
            this.serverName = serverName;
            this.updatedAt = updatedAt;
            var attributes = line.split("\\s+");
            this.fs = attributes[0];
            this.size = attributes[1];
            this.used = attributes[2];
            this.available = attributes[3];
            this.usePercentage = attributes[4];
            this.usePercentageValue = new BigDecimal(attributes[4].replace("%", ""));
            this.mountedOn = attributes[5];
        }

        public boolean isUsePercentageAbove(BigDecimal value) {
            return is(this.usePercentageValue, ">", value);
        }
    }

    public static ServerFileSystemMetadata success(List<FileSystemMetadataRow> rows) {
        return new ServerFileSystemMetadata(
                false,
                rows.stream().sorted(PERCENTAGE_REVERSED).toList(),
                "",
                "",
                ""
        );
    }

    public static ServerFileSystemMetadata failure(Throwable failureThrowable) {
        return new ServerFileSystemMetadata(
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
