package jbst.ops.server.services;

import com.jcraft.jsch.*;
import jbst.foundation.domain.time.TimeAmount;
import jbst.ops.server.domain.computed.ServerSshConfigs;
import jbst.ops.server.domain.ssh.SshSession;
import jbst.ops.server.exceptions.SshSessionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SshService {
    private static final TimeAmount SSH_CONNECTION_TIMEOUT = new TimeAmount(15L, SECONDS);
    private static final String JSCH_EXCEPTION = "Unexpected SSH config for host: `%s`";

    public final SshSession getSession(ServerSshConfigs serverSshConfigs) {
        try {
            if (nonNull(serverSshConfigs.getPassword())) {
                var jsch = new JSch();
                var session = jsch.getSession(serverSshConfigs.getUsername().value(), serverSshConfigs.getHost());
                session.setConfig(this.getConfigs());
                session.setPassword(serverSshConfigs.getPassword().value());
                return this.timeoutSshSession(session);
            } else if (nonNull(serverSshConfigs.getSshKey())) {
                var sshKeyPath = serverSshConfigs.getSshKeyPath();
                var sshKeyPassword = serverSshConfigs.getSshKeyPassword();
                var jsch = new JSch();
                jsch.addIdentity(sshKeyPath, sshKeyPassword.value());
                var session = jsch.getSession(serverSshConfigs.getUsername().value(), serverSshConfigs.getHost());
                session.setConfig(this.getConfigs());
                return this.timeoutSshSession(session);
            }
        } catch (JSchException ex) {
            return SshSession.failure(ex);
        }
        return SshSession.failure(new JSchException(String.format(JSCH_EXCEPTION, serverSshConfigs.getHost())));
    }

    public final List<String> executeCmd(Session session, String cmd) throws SshSessionException {
        try {
            var execChannel = (ChannelExec) session.openChannel("exec");
            execChannel.setErrStream(System.err);
            var in = execChannel.getInputStream();
            execChannel.setCommand(cmd);
            execChannel.connect();
            List<String> lines;
            try (var reader = new BufferedReader(new InputStreamReader(in))) {
                lines = new ArrayList<>();
                String line;
                var index = 0;
                while ((line = reader.readLine()) != null) {
                    LOGGER.debug("{} : {}", ++index, line);
                    lines.add(line);
                }
            }
            int exitStatus = execChannel.getExitStatus();
            LOGGER.debug("Exit status: {}", exitStatus);
            execChannel.disconnect();
            LOGGER.debug("Jsch {exec} channel: Disconnect");
            return lines;
        } catch (JSchException | IOException ex) {
            throw new SshSessionException(ex);
        }
    }

    public final void attachLogs(Session session, OutputStream os, String logsPath) throws SshSessionException {
        try {
            var sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            sftpChannel.get(logsPath, os);
            sftpChannel.disconnect();
            LOGGER.debug("Jsch {sftp} channel: Disconnect");
        } catch (JSchException | SftpException ex) {
            throw new SshSessionException(ex);
        }
    }

    // ================================================================================================================
    // Private Methods
    // ================================================================================================================
    private SshSession timeoutSshSession(Session session) {
        var executorService = Executors.newSingleThreadExecutor();
        var connectionCompleted = executorService.submit(() -> {
            try {
                session.connect();
                return SshSession.success(session);
            } catch (JSchException | RuntimeException ex) {
                return SshSession.failure(ex);
            }
        });
        SshSession sshSession;
        try {
            sshSession = connectionCompleted.get(SSH_CONNECTION_TIMEOUT.amount(), TimeUnit.of(SSH_CONNECTION_TIMEOUT.unit()));
        } catch (ExecutionException ex) {
            return SshSession.failure(ex.getCause());
        } catch (TimeoutException ex) {
            return SshSession.failure(ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return SshSession.failure(ex);
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executorService.shutdownNow();
        }
        return sshSession;
    }

    private Properties getConfigs() {
        var configs = new Properties();
        configs.put("StrictHostKeyChecking", "no");
        configs.put("PreferredAuthentications", "publickey,password");
        return configs;
    }
}
