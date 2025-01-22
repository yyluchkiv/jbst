package jbst.ops.server.domain.computed;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.ssh.SshConnectionConfigs;
import jbst.ops.server.domain.configs.ssh.SshConfigs;
import jbst.ops.server.domain.configs.ssh.SshConfigsFileSystem;
import jbst.ops.server.domain.servers.ServerFileSystemMetadata;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class ServerSshConfigs {
    private final SshConnectionConfigs connectionConfigs;
    // Filters
    private final SshConfigsFileSystem fileSystem;

    public ServerSshConfigs(
            SshConfigs sshConfigs,
            String sshKeyPath,
            Password sshKeyPassword
    ) {
        this.connectionConfigs = new SshConnectionConfigs(
                sshConfigs.username(),
                sshConfigs.host(),
                null,
                sshConfigs.sshKey(),
                sshKeyPath,
                sshKeyPassword
        );
        this.fileSystem = sshConfigs.fileSystem();
    }

    // WARNING: Please use sshKey/sshKeyPassword
    public ServerSshConfigs(
            SshConfigs sshConfigs,
            Password password
    ) {
        this.connectionConfigs = new SshConnectionConfigs(
                sshConfigs.username(),
                sshConfigs.host(),
                password,
                sshConfigs.sshKey(),
                null,
                null
        );
        this.fileSystem = sshConfigs.fileSystem();
    }

    public List<String> getFileSystemSkipByNameFilter() {
        if (nonNull(this.fileSystem) &&
            nonNull(this.fileSystem.filters()) &&
            nonNull(this.fileSystem.filters().skipByName())) {
            return this.fileSystem.filters().skipByName();
        } else {
            return new ArrayList<>();
        }
    }

    public boolean isFileSystemProcessable(ServerFileSystemMetadata.FileSystemMetadataRow row) {
        return !this.getFileSystemSkipByNameFilter().contains(row.getFs());
    }
}
