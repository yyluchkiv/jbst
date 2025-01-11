package jbst.ops.server.domain.configs;

import jbst.foundation.domain.base.ServerName;
import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.ops.server.domain.configs.ssh.SshConfigs;
import jbst.ops.server.domain.servers.ServerType;
import jbst.ops.server.domain.servers.Team;

import java.util.List;

public record ServerConfigs(
        boolean disableMonitoring,
        Team team,
        ServerType type,
        ServerName name,
        String ipAddress,
        List<String> allowedErrorMessages,
        List<String> aliases,
        SshConfigs sshConfigs,
        UsernamePasswordCredentials usernamePasswordCredentials
) {
}

