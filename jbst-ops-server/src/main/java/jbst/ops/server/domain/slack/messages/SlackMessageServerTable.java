package jbst.ops.server.domain.slack.messages;

import jbst.ops.server.domain.servers.Server;
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
public class SlackMessageServerTable {
    private final String value;

    public SlackMessageServerTable(List<Server> servers) {
        this.value = getSlackTable(
                SlackMessageServerRowLine.HEADER,
                servers.stream()
                        .map(SlackMessageServerRowLine::new)
                        .map(SlackMessageServerRowLine::getValue)
                        .sorted()
                        .collect(Collectors.joining(NEWLINE))
        );
    }
}
