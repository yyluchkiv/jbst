package jbst.server.ops.domain.slack.messages;

import jbst.server.ops.domain.servers.Server;
import jbst.server.ops.domain.servers.ServerFileSystemMetadata;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;
import static jbst.foundation.utilities.slack.SlackUtility.getSlackTable;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class SlackMessageFileSystemTable {
    private final String value;

    public SlackMessageFileSystemTable(List<ServerFileSystemMetadata.FileSystemMetadataRow> rows) {
        this.value = getSlackTable(
                SlackMessageFileSystemRowLine.HEADER_SERVERS_INCLUDED,
                rows.stream()
                        .map(SlackMessageFileSystemRowLine::new)
                        .map(SlackMessageFileSystemRowLine::getValue)
                        .collect(Collectors.joining(NEWLINE))
        );
    }

    public SlackMessageFileSystemTable(Server server) {
        var table = "";
        table += new SlackMessageServerRowLine(server).getValue();
        table += NEWLINE;
        table += NEWLINE;
        if (server.sshRequired()) {
            table += new SlackMessageFileSystemRow(server).getValue();
        }
        this.value = getSlackTable(
                SlackMessageServerRowLine.HEADER,
                table
        );
    }

    public static String getNoFsTable() {
        return getSlackTable(
                SlackMessageFileSystemRowLine.HEADER_SERVERS_INCLUDED,
                SlackMessageFileSystemRowLine.ROW_NO_ENTRIES
        );
    }
}
