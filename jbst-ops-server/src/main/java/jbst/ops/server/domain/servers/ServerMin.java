package jbst.ops.server.domain.servers;

import jbst.foundation.domain.base.ServerName;
import jbst.ops.server.domain.incidents.OpsIncidentEnv;

public record ServerMin(
        ServerName name,
        Team team,
        String ipAddress
) {

    public static ServerMin unexpected(Team team, OpsIncidentEnv opsIncidentEnv) {
        return new ServerMin(
                ServerName.dash(),
                team,
                opsIncidentEnv.getRemoteHost()
        );
    }
}
