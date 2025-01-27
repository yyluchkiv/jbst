package jbst.ops.server.domain.configs.servers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jbst.foundation.domain.base.ServerName;
import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.ops.server.domain.configs.ssh.SshConfigs;
import jbst.ops.server.domain.servers.ServerFileSystemMetadata;
import jbst.ops.server.domain.servers.ServerType;
import jbst.ops.server.domain.servers.Team;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

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

    @JsonIgnore
    public List<String> getFileSystemSkipByNameFilter() {
        var fileSystem = nonNull(this.sshConfigs) ? this.sshConfigs.fileSystem() : null;
        return nonNull(fileSystem) &&
               nonNull(fileSystem.filters()) &&
               nonNull(fileSystem.filters().skipByName()) ? fileSystem.filters().skipByName() : new ArrayList<>();
    }

    public boolean isFileSystemProcessable(ServerFileSystemMetadata.FileSystemMetadataRow row) {
        var skipByName = this.getFileSystemSkipByNameFilter();
        return !skipByName.contains(row.getFs());
    }

}

