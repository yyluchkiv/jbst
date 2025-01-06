package jbst.ops.server.exceptions;

import jbst.ops.server.domain.servers.Team;

public class ServerNotFoundException extends RuntimeException {

    public ServerNotFoundException(Integer serverId) {
        super("No server with serverId: `{" + serverId + "}`");
    }

    public ServerNotFoundException(Integer serverId, Team team) {
        super("No server with serverId: `{" + serverId + "}`. Team: `{" + team + "}`");
    }
}
