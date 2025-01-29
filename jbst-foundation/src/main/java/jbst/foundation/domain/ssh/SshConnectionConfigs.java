package jbst.foundation.domain.ssh;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.time.TimeAmount;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.time.temporal.ChronoUnit.SECONDS;

// Lombok
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class SshConnectionConfigs {
    @NotNull
    private final Username username;
    @NotNull
    private final String host;
    // Username + Password
    @Nullable
    private final Password password;
    // RSA SSH Key
    @Nullable
    private final String sshKey;
    @Nullable
    private final String sshKeyPath;
    @Nullable
    private final Password sshKeyPassword;
    // Timeout
    private final TimeAmount timeout = new TimeAmount(15L, SECONDS);}
