package jbst.ops.server.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jbst.foundation.domain.exceptions.base.JbstUnreachableCodeException;
import jbst.foundation.feigns.github.GithubClient;
import jbst.foundation.incidents.domain.Incident;
import jbst.ops.server.domain.computed.ServerInfinityTimerTask;
import jbst.ops.server.domain.computed.ServerInfinityTimerTaskSpringBeans;
import jbst.ops.server.domain.computed.ServerInfinityTimerTasks;
import jbst.ops.server.domain.configs.OpsConfigs;
import jbst.ops.server.domain.configs.ServerConfigs;
import jbst.ops.server.domain.incidents.OpsIncident;
import jbst.ops.server.domain.incidents.OpsIncidentEnv;
import jbst.ops.server.domain.servers.ServerMin;
import jbst.ops.server.domain.servers.Servers;
import jbst.ops.server.properties.OpsProperties;
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
import static jbst.ops.server.constants.OpsConstants.Logs.PREFIX;
import static jbst.ops.server.domain.servers.ServerType.SERVER_AS_FULL_SPRING_BOOT;
import static jbst.ops.server.properties.configs.ServersConfigs.Mode.GITHUB;
import static jbst.ops.server.properties.configs.ServersConfigs.Mode.RESOURCES;
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
    private final GithubClient githubClient;
    // Spring
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    // Properties
    private final OpsProperties opsProperties;

    private OpsConfigs opsConfigs;
    private ServerInfinityTimerTasks servers = new ServerInfinityTimerTasks(new ArrayList<>());

    public final void initialize() {
        LOGGER.info(PREFIX + " read servers. Status: {}", STARTED.formatAnsi());
        this.servers = this.initializeServersInfinityTimerTasks();
        LOGGER.info(PREFIX + " read servers. Status: {}", COMPLETED.formatAnsi());
    }

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

    // TODO [YYL] fixme
//    public final Servers getServers(Team team) {
//        return new Servers(
//                this.servers.values().stream()
//                        .filter(server -> nonNull(team) && team.equals(server.getServerConfigs().team()))
//                        .map(ServerInfinityTimerTask::getServer)
//                        .collect(Collectors.toList())
//        );
//    }

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
        // WARNING #1: 6001 - tehms
        // WARNING #2: find more efficient solution (E.G. add infrastructure.getServersSkipInfrastructure method)
        var skipPorts = Set.of(6001);
        // Incident -> Server
        var remoteHost = opsIncidentEnv.getRemoteHost();
        Map<String, ServerMin> serversMappedByTeams = new HashMap<>();
        this.servers.values().forEach(server -> {
            var team = server.getServerConfigs().team();
            var serverIpAddress = server.getIpAddress();
            var serverIpAddressURI = URI.create(serverIpAddress);
            if (!skipPorts.contains(serverIpAddressURI.getPort())) {
                var serverMin = new ServerMin(
                        server.getName(),
                        team,
                        serverIpAddress
                );

                serversMappedByTeams.put(serverIpAddressURI.getHost(), serverMin);
                var aliases = server.getServerConfigs().aliases();
                if (!isEmpty(aliases)) {
                    aliases.forEach(alias -> serversMappedByTeams.put(alias, serverMin));
                }
            }
        });
        var server = serversMappedByTeams.getOrDefault(remoteHost, ServerMin.unexpected(this.opsConfigs.mainTeam() ,opsIncidentEnv));
        return OpsIncident.of(
                incident,
                server,
                opsIncidentEnv,
                this.opsProperties.getRecipientsConfigs()
        );
    }

    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    private ServerInfinityTimerTasks initializeServersInfinityTimerTasks() {
        this.opsConfigs = this.readOpsConfigs();

        LOGGER.info(PREFIX + " github configuration. Servers: {}. Filtration: {}", this.opsConfigs.getServersCount(), STARTED.formatAnsi());
        this.opsConfigs.serversConfigs().removeIf(ServerConfigs::disableMonitoring);
        LOGGER.info(PREFIX + " github configuration. Servers: {}. Filtration: {}", this.opsConfigs.getServersCount(), COMPLETED.formatAnsi());

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
                                        this.opsProperties.getServersConfigs().getMonitoringConfigs(),
                                        this.serverInfinityTimerTaskSpringBeans,
                                        this.opsProperties.getServersConfigs().getRsaKeysBaseLocation(),
                                        this.opsConfigs.getMappedSshKeys()
                                )
                        ).collect(Collectors.toList())
        );
    }

    private OpsConfigs readOpsConfigs() {
        try {
            if (GITHUB.equals(this.opsProperties.getServersConfigs().getMode())) {
                var configuration = createTempFile("github-", "-contents");
                configuration.deleteOnExit();
                var gc = this.opsProperties.getServersConfigs().getGithubConfigs();
                copyURLToFile(
                        new URL(
                                this.githubClient.getContents(
                                        new GithubClient.GithubRepoContentsRequest(
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
            if (RESOURCES.equals(this.opsProperties.getServersConfigs().getMode())) {
                var resource = this.resourceLoader.getResource("classpath:ops-server-configs.json");
                var json = new String(resource.getInputStream().readAllBytes(), UTF_8);
                return this.objectMapper.readValue(json, OpsConfigs.class);
            }
        } catch (IOException ex) {
            LOGGER.error("Failure reading ops configs", ex);
        }
        throw new JbstUnreachableCodeException();
    }
}
