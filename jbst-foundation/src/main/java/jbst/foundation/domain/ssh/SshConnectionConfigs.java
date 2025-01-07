package jbst.foundation.domain.ssh;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.time.TimeAmount;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static java.time.temporal.ChronoUnit.SECONDS;

// TODO [YYL] add constructors/@Nullable
// Lombok
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class SshConnectionConfigs {
    private final Username username;
    private final String host;
    // Username + Password
    private final Password password;
    // RSA SSH Key
    private final String sshKey;
    private final String sshKeyPath;
    private final Password sshKeyPassword;
    // Timeout
    private final TimeAmount timeout = new TimeAmount(15L, SECONDS);

    public SshConnectionConfigs(Username username, String host, String sshKey, String sshKeyPath, Password sshKeyPassword) {
        this.username = username;
        this.host = host;
        this.password = null;
        this.sshKey = sshKey;
        this.sshKeyPath = sshKeyPath;
        this.sshKeyPassword = sshKeyPassword;
    }

    // WARNING: Please use sshKey/sshKeyPassword
    public SshConnectionConfigs(Username username, String host, Password password, String sshKey) {
        this.username = username;
        this.host = host;
        this.password = password;
        this.sshKey = sshKey;
        this.sshKeyPath = null;
        this.sshKeyPassword = null;
    }
}
