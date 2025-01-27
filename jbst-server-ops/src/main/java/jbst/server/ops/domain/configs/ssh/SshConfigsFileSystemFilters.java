package jbst.server.ops.domain.configs.ssh;

import java.util.List;

public record SshConfigsFileSystemFilters(
        List<String> skipByName
) {
}
