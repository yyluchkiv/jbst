package jbst.ops.server.domain.configs.ssh;

import jbst.foundation.domain.base.Username;

public record SshConfigs(
        Username username,
        String host,
        String sshKey,
        SshConfigsFileSystem fileSystem
) {
}
