package jbst.server.ops.domain.computed;

import jbst.foundation.domain.base.ServerName;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.ssh.JbstSSH;
import jbst.foundation.domain.ssh.SshConnectionConfigs;
import jbst.foundation.domain.states.AbstractClassicStateManager;
import jbst.foundation.domain.states.ClassicState;
import jbst.foundation.domain.time.JbstSchedulerConfiguration;
import jbst.foundation.feigns.spring.JbstSpringBoot;
import jbst.server.ops.domain.configs.servers.ServerConfigs;
import jbst.server.ops.domain.configs.ssh.SshRsaKey;
import jbst.server.ops.domain.servers.Server;
import jbst.server.ops.domain.servers.ServerFileSystemMetadata;
import jbst.server.ops.domain.servers.Team;
import jbst.server.ops.properties.base.JbstPropertyOpsServersMonitoring;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static jbst.foundation.domain.constants.JbstConstants.Strings.UNDEFINED;
import static jbst.foundation.domain.constants.JbstConstants.Symbols.DASH;
import static jbst.foundation.domain.cryptography.JbstEncoding.getBasicAuthenticationHeader;
import static jbst.foundation.domain.enums.Status.COMPLETED;
import static jbst.foundation.domain.enums.Status.STARTED;
import static jbst.foundation.domain.numbers.BigDecimalUtility.is;
import static jbst.foundation.domain.random.JbstRandom.randomIPv4;
import static jbst.foundation.domain.time.JbstSchedulerConfiguration.EVERY_30_SECONDS;
import static jbst.foundation.domain.time.JbstTime.convert;
import static jbst.foundation.domain.time.TimestampUtility.getCurrentTimestamp;
import static jbst.server.ops.constants.OpsConstants.Logs.PREFIX;

@Slf4j
// Lombok
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class ServerInfinityWorker {
    public static final JbstSchedulerConfiguration EVERY_1_HOUR = new JbstSchedulerConfiguration(1L, 60L, TimeUnit.MINUTES);

    private static class StateManager extends AbstractClassicStateManager {
        private final ServerConfigs serverConfigs;

        public StateManager(ClassicState state, ServerConfigs serverConfigs) {
            super(state);
            this.serverConfigs = serverConfigs;
        }

        @Override
        public String getLogKeyword() {
            return PREFIX + " Server: {}. State: {} → {}";
        }

        @Override
        public String getLogId() {
            return this.serverConfigs.name() + "@" + this.serverConfigs.team();
        }
    }

    // Configs [base]
    private final ServerConfigs serverConfigs;
    private final JbstPropertyOpsServersMonitoring serversMonitoringConfigs;
    private final ServerInfinityTimerTaskSpringBeans beans;
    private final Team mainTeam;

    // Configs [processed]
    private final boolean sshRequired;
    private final SshConnectionConfigs sshConnectionConfigs;
    private final boolean isSpringActuatorAuthenticationRequired;

    // Computed
    private ResponseEntity<JbstSpringBoot.SpringBootActuatorInfo> springBootActuatorInfo;
    private ResponseEntity<JbstSpringBoot.SpringBootActuatorHealth> springBootActuatorHealth;
    private boolean up;
    private CircularFifoQueue<Boolean> upHistory;
    private ServerFileSystemMetadata fileSystemMetadata;
    private Long onlineLastUpdatedAt;
    private Long sshLastUpdatedAt;

    // TimerTask
    private final StateManager stateManager;
    private StateManager getLock() {
        return this.stateManager;
    }

    private final ScheduledExecutorService onlineSES = newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService sshSES = newSingleThreadScheduledExecutor();

    public ServerInfinityWorker(
            ServerConfigs serverConfigs,
            JbstPropertyOpsServersMonitoring serversMonitoringConfigs,
            ServerInfinityTimerTaskSpringBeans beans,
            String rsaKeysBaseLocation,
            Map<String, SshRsaKey> mappedSshKeys,
            Team mainTeam
    ) {
        this.stateManager = new StateManager(ClassicState.CREATED, serverConfigs);

        // Configs [base]
        this.serverConfigs = serverConfigs;
        this.serversMonitoringConfigs = serversMonitoringConfigs;
        this.beans = beans;
        this.mainTeam = mainTeam;

        // Configs [processed]: attach SSH key password
        var sshConfigs = serverConfigs.sshConfigs();
        this.sshRequired = nonNull(sshConfigs);
        if (this.sshRequired) {
            this.sshConnectionConfigs = sshConfigs.asSshConnectionConfigs(rsaKeysBaseLocation, mappedSshKeys);
        } else {
            this.sshConnectionConfigs = null;
            LOGGER.debug(PREFIX + " SSH configs are missing. Server `{}`", this.serverConfigs.name());
        }

        // Configs [processed]: Spring Boot
        this.isSpringActuatorAuthenticationRequired = serverConfigs.type().isServerSpringBoot() &&
                nonNull(serverConfigs.usernamePasswordCredentials()) &&
                nonNull(serverConfigs.usernamePasswordCredentials().username()) &&
                nonNull(serverConfigs.usernamePasswordCredentials().password());

        // Computed: servers before verification is considered 'down' to receive notification at restart
        if (this.mainTeam.equals(serverConfigs.team())) {
            this.addUpEvent(false);
        }

        this.onlineTick();
        runAsync(this::sshTick);

        synchronized (this.getLock()) {
            if (!this.stateManager.getState().getPermissions().startPermitted()) {
                return;
            }
            this.stateManager.start();

            this.onlineSES.scheduleWithFixedDelay(
                    this::onlineTick,
                    EVERY_30_SECONDS.initialDelay(),
                    EVERY_30_SECONDS.delay(),
                    EVERY_30_SECONDS.unit()
            );

            if (this.sshRequired) {
                this.sshSES.scheduleWithFixedDelay(
                        this::sshTick,
                        EVERY_1_HOUR.initialDelay(),
                        EVERY_1_HOUR.delay(),
                        EVERY_1_HOUR.unit()
                );
            }

            this.stateManager.onActivation();
        }
    }

    public void addUpEvent(Boolean event) {
        if (isNull(this.upHistory)) {
            // up running events (previous, current)
            this.upHistory = new CircularFifoQueue<>(2);
        }
        this.upHistory.add(event);
        this.setUp(event);
    }

    public boolean isErrorMessageAllowed(String errorMessage) {
        var allowedErrorMessages = this.serverConfigs.allowedErrorMessages();
        return nonNull(allowedErrorMessages) &&
                (allowedErrorMessages.contains(errorMessage) || // HTTP 1.1
                allowedErrorMessages.stream().anyMatch(errorMessage::startsWith)); // HTTP 2
    }

    public JbstSpringBoot.SpringBootActuatorInfo springBootActuatorInfoEndpointResponse() {
        return nonNull(this.springBootActuatorInfo) ? this.springBootActuatorInfo.getBody() : JbstSpringBoot.SpringBootActuatorInfo.dash();
    }

    public String getHealthAsString() {
        if (!this.up) {
            return "✕";
        }
        var serverType = this.getServerConfigs().type();
        if (serverType.isServerPing()) {
            var allowedErrorMessages = this.serverConfigs.allowedErrorMessages();
            return isNull(allowedErrorMessages) || allowedErrorMessages.isEmpty() ? "✓" : "[4**] ✓";
        } else if (serverType.isServerSpringBoot()) {
            return this.isSpringBootHealthy() ? "✓" : "✕";
        } else {
            return UNDEFINED;
        }
    }

    public boolean isOk() {
        var serverType = this.getServerConfigs().type();
        if (serverType.isServerPing()) {
            return this.up;
        } else if (serverType.isServerSpringBoot()) {
            return this.up && this.isSpringBootHealthy();
        } else {
            return false;
        }
    }

    public boolean isAnyChanges() {
        var copyOfUpHistory = this.getUpHistory();
        if (isNull(copyOfUpHistory)) {
            return false;
        }
        var size = copyOfUpHistory.size();
        // 2-size queue (only current and previous state of running is stored)
        var current = copyOfUpHistory.get(0);
        if (size == 1) {
            return !current;
        }
        var previous = copyOfUpHistory.get(1);
        return !current.equals(previous);
    }

    public boolean fileSystemMetadataThresholdReached() {
        return this.sshRequired &&
                nonNull(this.fileSystemMetadata) &&
                this.fileSystemMetadata.rows().stream().anyMatch(row -> is(row.getUsePercentageValue(), ">", this.serversMonitoringConfigs.getFileSystemThreshold()));
    }

    public boolean fileSystemMetadataProblems() {
        return this.sshRequired &&
                nonNull(this.fileSystemMetadata) &&
                (this.fileSystemMetadata.failure() || this.fileSystemMetadataThresholdReached());
    }

    public ServerName getName() {
        return this.serverConfigs.name();
    }

    public String getIpAddress() {
        return this.serverConfigs.ipAddress();
    }

    // ================================================================================================================
    // COMPUTING: Online
    // ================================================================================================================

    public final void onlineTick() {
        try {
            var serverType = this.serverConfigs.type();
            if (serverType.isServerPing()) {
                this.addServerStatusAsPing();
            }
            if (serverType.isServerSpringBoot()) {
                this.addServerStatusAsSpringBoot();
            }
        } catch (RuntimeException ex) {
            this.addUpEvent(false);
        }
    }

    private void addServerStatusAsPing() {
        try {
            this.beans.getRestTemplate().getForEntity(this.getIpAddress(), String.class);
            this.addUpEvent(true);
        } catch (HttpClientErrorException ex) {
            var upEvent = this.isErrorMessageAllowed(ex.getMessage());
            this.addUpEvent(upEvent);
        } catch (ResourceAccessException | HttpServerErrorException | UnknownHttpStatusCodeException ex) {
            this.addUpEvent(false);
        }
        this.onlineLastUpdatedAt = getCurrentTimestamp();
    }

    private void addServerStatusAsSpringBoot() {
        // Headers
        var httpHeaders = new HttpHeaders();
        if (this.isSpringActuatorAuthenticationRequired) {
            var basicAuthenticationHeader = getBasicAuthenticationHeader(
                    this.getServerConfigs().usernamePasswordCredentials().username().value(),
                    this.getServerConfigs().usernamePasswordCredentials().password().value()
            );
            httpHeaders.set(basicAuthenticationHeader.a(), basicAuthenticationHeader.b());
        }
        var httpEntity = new HttpEntity<>(httpHeaders);

        try {
            this.springBootActuatorHealth = this.beans.getRestTemplate().exchange(
                    this.getIpAddress() + "/actuator/health",
                    HttpMethod.GET,
                    httpEntity,
                    JbstSpringBoot.SpringBootActuatorHealth.class
            );
            this.addUpEvent(true);
        } catch (ResourceAccessException | HttpClientErrorException | HttpServerErrorException | UnknownHttpStatusCodeException ex) {
            this.springBootActuatorHealth = ResponseEntity.internalServerError().build();
            this.addUpEvent(false);
        }

        try {
            this.springBootActuatorInfo = this.beans.getRestTemplate().exchange(
                    this.getIpAddress() + "/actuator/info",
                    HttpMethod.GET,
                    httpEntity,
                    JbstSpringBoot.SpringBootActuatorInfo.class
            );
        } catch (ResourceAccessException | HttpServerErrorException | HttpClientErrorException | UnknownHttpStatusCodeException ex) {
            this.springBootActuatorInfo = ResponseEntity.internalServerError().build();
        }
        this.onlineLastUpdatedAt = getCurrentTimestamp();
    }

    // ================================================================================================================
    // COMPUTING: SSH
    // ================================================================================================================
    public final void sshTick() {
        try {
            if (this.sshRequired) {
                this.ssh();
            }
        } catch (JbstExceptions.SshSession | RuntimeException ex) {
            this.fileSystemMetadata = ServerFileSystemMetadata.failure(ex);
        }
    }

    private void ssh() throws JbstExceptions.SshSession {
        LOGGER.info(PREFIX + " SSH into server {}. Status: {}", this.getName(), STARTED.asANSI());
        var sshSession = JbstSSH.getSession(this.sshConnectionConfigs);
        if (sshSession.getSession().present()) {
            this.sshLastUpdatedAt = getCurrentTimestamp();
            var lines = JbstSSH.executeCmd(sshSession.getSession().value(), "df -h");
            var rows = lines.stream()
                    .skip(1)
                    .map(line -> new ServerFileSystemMetadata.FileSystemMetadataRow(this.getName(), this.getTimeOrDash(this.sshLastUpdatedAt), line))
                    .filter(this.serversMonitoringConfigs::isFileSystemProcessable)
                    .filter(this.serverConfigs::isFileSystemProcessable)
                    .collect(Collectors.toList());
            this.fileSystemMetadata = ServerFileSystemMetadata.success(rows);
        } else {
            this.fileSystemMetadata = ServerFileSystemMetadata.failure(sshSession.getThrowable().value());
        }
        LOGGER.info(PREFIX + " SSH into server {}. Status: {}", this.getName(), COMPLETED.asANSI());
    }

    // ================================================================================================================
    // Section: Server
    // ================================================================================================================
    public Server getServer() {
        return new Server(
                this.serverConfigs.team(),
                this.serverConfigs.type(),
                this.serverConfigs.name(),
                this.serversMonitoringConfigs.isHideIP() ? randomIPv4() : this.getIpAddress(),
                this.isOk(),
                this.getHealthAsString(),
                this.isAnyChanges(),
                this.upHistory,
                this.getTimeOrDash(this.onlineLastUpdatedAt),
                this.springBootActuatorInfoEndpointResponse(),
                this.sshRequired,
                this.fileSystemMetadataProblems(),
                this.fileSystemMetadataThresholdReached(),
                this.fileSystemMetadata,
                this.getTimeOrDash(this.sshLastUpdatedAt)
        );
    }

    // ================================================================================================================
    // PRIVATE
    // ================================================================================================================
    private String getTimeOrDash(Long timestamp) {
        if (nonNull(timestamp)) {
            return convert(timestamp, this.serversMonitoringConfigs.getZoneId()).format(JbstConstants.DateTimeFormatters.DTF51);
        } else {
            return DASH;
        }
    }

    private boolean isSpringBootHealthy() {
        if (isNull(this.springBootActuatorHealth) || isNull(this.springBootActuatorHealth.getBody()) || isNull(this.springBootActuatorHealth.getBody().status())) {
            return false;
        }
        return Status.UP.equals(this.springBootActuatorHealth.getBody().status());
    }
}

