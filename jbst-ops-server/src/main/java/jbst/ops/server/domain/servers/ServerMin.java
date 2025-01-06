package jbst.ops.server.domain.servers;

import jbst.foundation.domain.base.ServerName;
import jbst.ops.server.domain.incidents.OpsIncidentEnv;

import static jbst.ops.server.domain.servers.IncidentsNotificationsMetadata.incidentNotificationsNoMetadata;

public record ServerMin(
        ServerName name,
        Team team,
        String ipAddress,
        IncidentsNotificationsMetadata incidentsNotificationsMetadata
) {

    public static ServerMin unexpected(OpsIncidentEnv opsIncidentEnv) {
        return new ServerMin(
                ServerName.dash(),
                Team.tech1(),
                opsIncidentEnv.getRemoteHost(),
                incidentNotificationsNoMetadata()
        );
    }
}
