package jbst.ops.server.domain.computed;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.ssh.SshConnectionConfigs;
import jbst.ops.server.domain.configs.ssh.SshConfigs;
import jbst.ops.server.domain.configs.ssh.SshConfigsFileSystem;
import jbst.ops.server.domain.configs.ssh.SshConfigsLogs;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class ServerSshConfigs {
    private final Username username;
    private final String host;
    // Username + Password
    private final Password password;
    // RSA SSH Key (originally password is in opsConfigs sshKeys mapping)
    private final String sshKey;
    private final String sshKeyPath;
    private final Password sshKeyPassword;
    // Filters
    private final SshConfigsLogs logs;
    private final SshConfigsFileSystem fileSystem;

    public SshConnectionConfigs getConnectionConfigs() {
        return new SshConnectionConfigs(
                this.username,
                this.host,
                this.password,
                this.sshKey,
                this.sshKeyPath,
                this.sshKeyPassword
        );
    }

    public ServerSshConfigs(
            SshConfigs sshConfigs,
            String sshKeyPath,
            Password sshKeyPassword
    ) {
        this.username = sshConfigs.username();
        this.host = sshConfigs.host();
        this.password = null;
        this.sshKey = sshConfigs.sshKey();
        this.sshKeyPath = sshKeyPath;
        this.sshKeyPassword = sshKeyPassword;
        this.logs = sshConfigs.logs();
        this.fileSystem = sshConfigs.fileSystem();
    }

    // WARNING: Please use sshKey/sshKeyPassword
    public ServerSshConfigs(
            SshConfigs sshConfigs,
            Password password
    ) {
        this.username = sshConfigs.username();
        this.host = sshConfigs.host();
        this.password = password;
        this.sshKey = sshConfigs.sshKey();
        this.sshKeyPath = null;
        this.sshKeyPassword = null;
        this.logs = sshConfigs.logs();
        this.fileSystem = sshConfigs.fileSystem();
    }
}
