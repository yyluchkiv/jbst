package jbst.server.ops.domain.configs.ssh;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.ssh.JbstSshConnectionConfigs;

import java.util.Map;

public record SshConfigs(
        Username username,
        String host,
        String sshKey,
        SshConfigsFileSystem fileSystem
) {

    public JbstSshConnectionConfigs asSshConnectionConfigs(
            String rsaKeysBaseLocation,
            Map<String, SshRsaKey> mappedSshKeys
    ) {
        var rsaKey = mappedSshKeys.get(this.sshKey);
        return new JbstSshConnectionConfigs(
                this.username,
                this.host,
                null,
                this.sshKey,
                rsaKeysBaseLocation + rsaKey.path() + this.sshKey,
                rsaKey.password()
        );
    }

}
