package jbst.ops.server.domain.servers;

import jbst.foundation.domain.base.ServerName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Comparator;

// Lombok
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class FileSystemMetadataRow {
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
}
