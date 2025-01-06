package jbst.ops.server.domain.slack.messages;

import jbst.foundation.domain.base.ServerName;
import jbst.foundation.domain.tuples.Tuple2;
import jbst.foundation.feigns.spring.domain.SpringBootActuatorInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;
import static jbst.foundation.utilities.strings.StringUtility.getShortenValueOrUndefined;
import static jbst.foundation.utilities.strings.StringUtility.toObjectsArray;
import static jbst.ops.server.utils.SlackUtils.getSlackTable;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class SlackMessageServersSpringActuatorsTable {
    private static final String FORMAT = "%-35s %19s %10s %10s %10s %25s";

    private final String value;

    // WARNING: when too many server -> split into table/row
    public SlackMessageServersSpringActuatorsTable(List<Tuple2<ServerName, SpringBootActuatorInfo>> mappedActuatorsResponses) {
        var table = mappedActuatorsResponses.stream()
                .map(tuple2 -> {
                    List<String> row = new ArrayList<>();
                    var serverName = tuple2.a();
                    if (isNull(serverName)) {
                        serverName = ServerName.dash();
                    }
                    var infoEndpointResponse = tuple2.b();
                    if (isNull(infoEndpointResponse)) {
                        infoEndpointResponse = SpringBootActuatorInfo.dash();
                    }
                    var git = infoEndpointResponse.getGitOrDash();
                    row.add(getShortenValueOrUndefined(serverName.value(), 35));
                    row.add(getShortenValueOrUndefined(infoEndpointResponse.getMavenVersionOrDash().value(), 19));
                    row.add(getShortenValueOrUndefined(infoEndpointResponse.getProfileOrDash(), 10));
                    row.add(getShortenValueOrUndefined(git.branch(), 10));
                    row.add(getShortenValueOrUndefined(git.commit().id(), 10));
                    row.add(getShortenValueOrUndefined(git.commit().time(), 25));
                    return format(FORMAT, toObjectsArray(row));
                })
                .sorted()
                .collect(Collectors.joining(NEWLINE));
        this.value = getSlackTable(
                getHeader(),
                table
        );
    }

    public static String getHeader() {
        return format(FORMAT, "Server", "Version", "Profile", "Branch", "CommitId", "CommitTime");
    }
}
