package jbst.server.ops.domain.slack.messages;

import jbst.server.ops.domain.servers.ServerFileSystemMetadata;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

import static java.lang.String.format;
import static jbst.foundation.domain.constants.JbstConstants.Symbols.DASH;
import static jbst.foundation.domain.strings.JbstStrings.getShortenValueOrUndefined;
import static jbst.foundation.domain.strings.JbstStrings.toObjectsArray;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class SlackMessageFileSystemRowLine {
    private static final String FORMAT = "%-35s %10s %12s %10s %10s %10s %25s %20s";

    private static final List<String> SERVERS_INCLUDED = List.of("Server", "Used %", "Available", "Used", "Size", "SSHed", "Mounted On", "File System");
    private static final List<String> SERVERS_EXCLUDED = List.of("", "Used %", "Available", "Used", "Size", "SSHed", "Mounted On", "File System");

    public static final String HEADER_SERVERS_INCLUDED = format(FORMAT, toObjectsArray(SERVERS_INCLUDED));
    public static final String HEADER_SERVERS_EXCLUDED = format(FORMAT, toObjectsArray(SERVERS_EXCLUDED));
    public static final String ROW_NO_ENTRIES = format(FORMAT, toObjectsArray(List.of("No entries", DASH, DASH, DASH, DASH, DASH, DASH, DASH)));

    private final String value;

    public SlackMessageFileSystemRowLine(ServerFileSystemMetadata.FileSystemMetadataRow row) {
        this.value = format(
                FORMAT,
                toObjectsArray(
                        List.of(
                                getShortenValueOrUndefined(row.getServerName().value(), 35),
                                getShortenValueOrUndefined(row.getUsePercentage(), 10),
                                getShortenValueOrUndefined(row.getAvailable(), 12),
                                getShortenValueOrUndefined(row.getUsed(), 10),
                                getShortenValueOrUndefined(row.getSize(), 10),
                                getShortenValueOrUndefined(row.getUpdatedAt(), 19),
                                getShortenValueOrUndefined(row.getMountedOn(), 25),
                                getShortenValueOrUndefined(row.getFs(), 20)
                        )
                )
        );
    }
}
