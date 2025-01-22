package jbst.ops.server.domain.configs.ssh;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.ssh.SshConnectionConfigs;

import java.util.Map;

public record SshConfigs(
        Username username,
        String host,
        String sshKey,
        SshConfigsFileSystem fileSystem
) {

    public SshConnectionConfigs asSshConnectionConfigs(
            String rsaKeysBaseLocation,
            Map<String, SshRsaKey> mappedSshKeys
    ) {
        var rsaKey = mappedSshKeys.get(this.sshKey);
        return new SshConnectionConfigs(
                this.username,
                this.host,
                null,
                this.sshKey,
                rsaKeysBaseLocation + rsaKey.path() + this.sshKey,
                rsaKey.password()
        );
    }

}
