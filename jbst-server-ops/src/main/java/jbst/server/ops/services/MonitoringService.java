package jbst.server.ops.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jbst.foundation.domain.base.ServerName;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.feigns.github.JbstGithub;
import jbst.foundation.incidents.domain.Incident;
import jbst.server.ops.domain.computed.ServerInfinityTimerTask;
import jbst.server.ops.domain.computed.ServerInfinityTimerTaskSpringBeans;
import jbst.server.ops.domain.computed.ServerInfinityTimerTasks;
import jbst.server.ops.domain.configs.OpsConfigs;
import jbst.server.ops.domain.configs.servers.ServerConfigs;
import jbst.server.ops.domain.incidents.OpsIncident;
import jbst.server.ops.domain.incidents.OpsIncidentEnv;
import jbst.server.ops.domain.servers.Server;
import jbst.server.ops.domain.servers.Servers;
import jbst.server.ops.properties.ServerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.io.File.createTempFile;
import static java.nio.charset.Charset.defaultCharset;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.nonNull;
import static jbst.foundation.domain.enums.Status.COMPLETED;
import static jbst.foundation.domain.enums.Status.STARTED;
import static jbst.server.ops.constants.OpsConstants.Logs.PREFIX;
import static jbst.server.ops.domain.servers.ServerType.SERVER_AS_FULL_SPRING_BOOT;
import static jbst.server.ops.properties.configs.JbstPropertyOpsServers.Mode.GITHUB;
import static jbst.server.ops.properties.configs.JbstPropertyOpsServers.Mode.RESOURCES;
import static org.apache.commons.io.FileUtils.copyURLToFile;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MonitoringService {
    // Spring Publisher
    private final ApplicationEventPublisher applicationEventPublisher;
    // Computing
    private final ServerInfinityTimerTaskSpringBeans serverInfinityTimerTaskSpringBeans;
    // Clients
    private final JbstGithub github;
    // Spring
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    // Properties
    private final ServerProperties serverProperties;

    private OpsConfigs opsConfigs;
    private ServerInfinityTimerTasks servers = new ServerInfinityTimerTasks(new ArrayList<>());

    public final void initialize() {
        LOGGER.info(PREFIX + " read servers. Status: {}", STARTED.asANSI());
        this.servers = this.initializeServersInfinityTimerTasks();
        LOGGER.info(PREFIX + " read servers. Status: {}", COMPLETED.asANSI());
    }

    @SuppressWarnings("unused")
    public final boolean isConfigured() {
        return this.servers.values().stream()
                .filter(ServerInfinityTimerTask::isSshRequired)
                .allMatch(server -> nonNull(server.getFileSystemMetadata()));
    }

    public final Servers getServers() {
        return new Servers(
                this.servers.values().stream()
                        .map(ServerInfinityTimerTask::getServer)
                        .collect(Collectors.toList())
        );
    }

    public final Servers getServersAnyChanges() {
        return new Servers(
                this.servers.values().stream()
                        .filter(ServerInfinityTimerTask::isAnyChanges)
                        .map(ServerInfinityTimerTask::getServer)
                        .collect(Collectors.toList())
        );
    }

    public final Servers getServersSpringBoot() {
        return new Servers(
                this.servers.values().stream()
                        .filter(server -> server.getServerConfigs().type().equals(SERVER_AS_FULL_SPRING_BOOT))
                        .map(ServerInfinityTimerTask::getServer)
                        .collect(Collectors.toList())
        );
    }

    public final Servers getServersSshRequired() {
        return new Servers(
                this.servers.values().stream()
                        .filter(ServerInfinityTimerTask::isSshRequired)
                        .map(ServerInfinityTimerTask::getServer)
                        .collect(Collectors.toList())
        );
    }

    public final Servers getServersSshRequiredAnyProblemsOnFsMetadata() {
        return new Servers(
                this.servers.values().stream()
                        .filter(ServerInfinityTimerTask::isSshRequired)
                        .filter(ServerInfinityTimerTask::fileSystemMetadataProblems)
                        .map(ServerInfinityTimerTask::getServer)
                        .collect(Collectors.toList())
        );
    }

    public final boolean isAnyChanges() {
        return this.getServers().isAnyChanges();
    }

    public final OpsIncident getOpsIncident(Incident incident, OpsIncidentEnv opsIncidentEnv) {
        Map<String, Server> serversHosts = new HashMap<>();
        // WARNING #1: 6001 - hms
        var skipPorts = Set.of(6001);
        this.servers.values().forEach(serverTask -> {
            var serverIpAddress = serverTask.getIpAddress();
            var serverIpAddressURI = URI.create(serverIpAddress);
            if (!skipPorts.contains(serverIpAddressURI.getPort())) {
                serversHosts.put(serverIpAddressURI.getHost(), serverTask.getServer());
                var aliases = serverTask.getServerConfigs().aliases();
                if (!isEmpty(aliases)) {
                    aliases.forEach(alias -> serversHosts.put(alias, serverTask.getServer()));
                }
            }
        });
        var remoteHost = opsIncidentEnv.getRemoteHost();
        var server = serversHosts.get(remoteHost);
        return OpsIncident.of(
                incident,
                nonNull(server) ? server.name() : ServerName.dash(),
                nonNull(server) ? server.team() : this.serverProperties.getSlacks().getMainTeam(),
                nonNull(server) ? server.ipAddress() : "[unknown]",
                opsIncidentEnv,
                this.serverProperties.getRecipients()
        );
    }

    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    private ServerInfinityTimerTasks initializeServersInfinityTimerTasks() {
        this.opsConfigs = this.readOpsConfigs();

        LOGGER.info(PREFIX + " github configuration. Servers: {}. Filtration: {}", this.opsConfigs.getServersCount(), STARTED.asANSI());
        this.opsConfigs.serversConfigs().removeIf(ServerConfigs::disableMonitoring);
        LOGGER.info(PREFIX + " github configuration. Servers: {}. Filtration: {}", this.opsConfigs.getServersCount(), COMPLETED.asANSI());

        if (this.opsConfigs.isAnyUnexpectedServersTeams()) {
            this.applicationEventPublisher.publishEvent(this.opsConfigs.getIncidentUnexpectedTeams());
        }

        if (this.opsConfigs.isAnyUnexpectedSshKeys()) {
            this.applicationEventPublisher.publishEvent(this.opsConfigs.getIncidentUnexpectedSshKeys());
        }

        return new ServerInfinityTimerTasks(
                this.opsConfigs.serversConfigs().stream()
                        .map(serverConfigs ->
                                new ServerInfinityTimerTask(
                                        serverConfigs,
                                        this.serverProperties.getServers().getMonitoring(),
                                        this.serverInfinityTimerTaskSpringBeans,
                                        this.serverProperties.getServers().getRsaKeysBaseLocation(),
                                        this.opsConfigs.getMappedSshKeys(),
                                        this.serverProperties.getSlacks().getMainTeam()
                                )
                        ).collect(Collectors.toList())
        );
    }

    private OpsConfigs readOpsConfigs() {
        try {
            if (GITHUB.equals(this.serverProperties.getServers().getMode())) {
                var configuration = createTempFile("github-", "-contents");
                configuration.deleteOnExit();
                var gc = this.serverProperties.getServers().getGithub();
                copyURLToFile(
                        new URL(
                                this.github.getContents(
                                        new JbstGithub.GithubRepoContentsRequest(
                                                gc.getToken(),
                                                gc.getOwner(),
                                                gc.getRepo(),
                                                gc.getContent()
                                        )
                                ).downloadUrl()
                        ),
                        configuration
                );
                var json = readFileToString(configuration, defaultCharset());
                return this.objectMapper.readValue(json, OpsConfigs.class);
            }
            if (RESOURCES.equals(this.serverProperties.getServers().getMode())) {
                var resource = this.resourceLoader.getResource("classpath:ops-server-configs.json");
                var json = new String(resource.getInputStream().readAllBytes(), UTF_8);
                return this.objectMapper.readValue(json, OpsConfigs.class);
            }
        } catch (IOException ex) {
            LOGGER.error("Failure reading ops configs", ex);
        }
        throw new JbstExceptions.UnreachableCode();
    }
}
