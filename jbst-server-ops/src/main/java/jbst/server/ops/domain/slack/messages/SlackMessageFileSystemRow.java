package jbst.server.ops.domain.slack.messages;

import jbst.server.ops.domain.servers.Server;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class SlackMessageFileSystemRow {
    private final String value;

    public SlackMessageFileSystemRow(Server server) {
        List<String> lines = new ArrayList<>();
        var fileSystemMetadata = server.fileSystemMetadata();
        if (fileSystemMetadata.failure()) {
            lines.add("SSH Connection Failure");
            lines.add("Message: " + fileSystemMetadata.failureMessage());
            lines.add("Class: " + fileSystemMetadata.failureClass());
            lines.add("Trace: " + fileSystemMetadata.failureTrace());
        } else {
            lines.add(SlackMessageFileSystemRowLine.HEADER_SERVERS_EXCLUDED);
            lines.add(
                    fileSystemMetadata.rows().stream()
                            .map(SlackMessageFileSystemRowLine::new)
                            .map(SlackMessageFileSystemRowLine::getValue)
                            .collect(Collectors.joining(NEWLINE))
            );
        }
        this.value = String.join(NEWLINE, lines);
    }
}
