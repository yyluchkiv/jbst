package jbst.ops.server.domain.slack.messages;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import jbst.ops.server.domain.servers.Server;

import java.util.List;
import java.util.stream.Collectors;

import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;
import static jbst.ops.server.utils.SlackUtils.getSlackTable;

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
