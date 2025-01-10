package jbst.ops.server.domain.servers;

import jbst.foundation.domain.base.ServerName;
import jbst.ops.server.domain.incidents.OpsIncidentEnv;

import static jbst.ops.server.domain.servers.IncidentsNotificationsMetadata.incidentNotificationsNoMetadata;

public record ServerMin(
        ServerName name,
        TeamV2 team,
        String ipAddress,
        IncidentsNotificationsMetadata incidentsNotificationsMetadata
) {

    // TODO [YYL] delete me
    public static ServerMin unexpected(OpsIncidentEnv opsIncidentEnv) {
        return new ServerMin(
                ServerName.dash(),
                new TeamV2("TECH1"),
                opsIncidentEnv.getRemoteHost(),
                incidentNotificationsNoMetadata()
        );
    }
}
