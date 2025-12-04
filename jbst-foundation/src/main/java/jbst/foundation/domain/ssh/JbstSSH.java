package jbst.foundation.domain.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import jbst.foundation.domain.exceptions.JbstExceptions;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.Objects.nonNull;

@Slf4j
@UtilityClass
public class JbstSSH {

    @SuppressWarnings("DataFlowIssue")
    public static JbstSshSession getSession(JbstSshConnectionConfigs connectionConfigs) {
        try {
            if (nonNull(connectionConfigs.getPassword())) {
                var jsch = new JSch();
                var session = jsch.getSession(connectionConfigs.getUsername().value(), connectionConfigs.getHost());
                session.setConfig(getConfigs());
                session.setPassword(connectionConfigs.getPassword().value());
                return timeoutSshSession(connectionConfigs, session);
            } else if (nonNull(connectionConfigs.getSshKey())) {
                var sshKeyPath = connectionConfigs.getSshKeyPath();
                var sshKeyPassword = connectionConfigs.getSshKeyPassword();
                var jsch = new JSch();
                jsch.addIdentity(sshKeyPath, sshKeyPassword.value());
                var session = jsch.getSession(connectionConfigs.getUsername().value(), connectionConfigs.getHost());
                session.setConfig(getConfigs());
                return timeoutSshSession(connectionConfigs, session);
            }
        } catch (JSchException ex) {
            return JbstSshSession.failure(ex);
        }
        return JbstSshSession.failure(new JSchException(String.format("Unexpected SSH config for host: %s", connectionConfigs.getHost())));
    }

    public static List<String> executeCmd(Session session, String cmd) throws JbstExceptions.SshSession {
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
            throw new JbstExceptions.SshSession(ex);
        }
    }

    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    private static JbstSshSession timeoutSshSession(JbstSshConnectionConfigs connectionConfigs, Session session) {
        var executorService = Executors.newSingleThreadExecutor();
        var connectionCompleted = executorService.submit(() -> {
            try {
                session.connect();
                return JbstSshSession.success(session);
            } catch (JSchException | RuntimeException ex) {
                return JbstSshSession.failure(ex);
            }
        });
        JbstSshSession sshSession;
        try {
            sshSession = connectionCompleted.get(connectionConfigs.getTimeout().amount(), TimeUnit.of(connectionConfigs.getTimeout().unit()));
        } catch (ExecutionException ex) {
            return JbstSshSession.failure(ex.getCause());
        } catch (TimeoutException ex) {
            return JbstSshSession.failure(ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return JbstSshSession.failure(ex);
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

    private static Properties getConfigs() {
        var configs = new Properties();
        configs.put("StrictHostKeyChecking", "no");
        configs.put("PreferredAuthentications", "publickey,password");
        return configs;
    }
}
