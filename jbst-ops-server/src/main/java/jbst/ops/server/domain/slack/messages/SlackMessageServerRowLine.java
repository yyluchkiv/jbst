package jbst.ops.server.domain.slack.messages;

import jbst.ops.server.domain.servers.Server;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

import static jbst.foundation.utilities.strings.StringUtility.getShortenValueOrUndefined;
import static jbst.foundation.utilities.strings.StringUtility.toObjectsArray;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class SlackMessageServerRowLine {
    private static final String FORMAT = "%-35s %10s %10s %58s %10s %10s";
    public static final String HEADER = FORMAT.formatted("Server", "Health", "Online", "IP Address", "ServerId", "SSHed");

    private final String value;

    public SlackMessageServerRowLine(Server server) {
        this.value = String.format(
                FORMAT,
                toObjectsArray(
                        List.of(
                                getShortenValueOrUndefined(server.name().value(), 35),
                                getShortenValueOrUndefined(server.health(), 10),
                                getShortenValueOrUndefined(server.onlineLastUpdatedAt(), 10),
                                getShortenValueOrUndefined(server.ipAddress(), 58),
                                getShortenValueOrUndefined(server.id().value(), 10),
                                getShortenValueOrUndefined(server.sshLastUpdatedAt(), 10)
                        )
                )
        );
    }
}
