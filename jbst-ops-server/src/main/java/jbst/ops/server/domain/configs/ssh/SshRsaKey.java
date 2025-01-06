package jbst.ops.server.domain.configs.ssh;

import jbst.foundation.domain.base.Password;

public record SshRsaKey(
        String name,
        String path,
        Password password
) {
}
