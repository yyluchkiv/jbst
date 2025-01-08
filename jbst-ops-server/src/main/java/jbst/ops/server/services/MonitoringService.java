package jbst.ops.server.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jbst.foundation.feigns.github.GithubClient;
import jbst.foundation.incidents.domain.Incident;
import jbst.ops.server.domain.computed.ServerInfinityTimerTask;
import jbst.ops.server.domain.computed.ServerInfinityTimerTaskSpringBeans;
import jbst.ops.server.domain.computed.ComputedServers;
import jbst.ops.server.domain.configs.OpsConfigs;
import jbst.ops.server.domain.configs.ServerConfigs;
import jbst.ops.server.domain.incidents.OpsIncident;
import jbst.ops.server.domain.incidents.OpsIncidentEnv;
import jbst.ops.server.domain.servers.ServerMin;
import jbst.ops.server.domain.servers.Servers;
import jbst.ops.server.domain.servers.Team;
import jbst.ops.server.exceptions.ServerNotFoundException;
import jbst.ops.server.properties.OpsProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
import static java.util.Objects.nonNull;
import static jbst.foundation.domain.enums.Status.COMPLETED;
import static jbst.foundation.domain.enums.Status.STARTED;
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
    // Mapper
    private final ObjectMapper objectMapper;
    // Properties
    private final OpsProperties opsProperties;

    private ComputedServers servers = new ComputedServers(new ArrayList<>());

    public final void readOpsConfigs() throws IOException {
        LOGGER.warn("[Server]: read ops configs");
        this.servers = this.readComputedServers();
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

    public final Servers getServers(Team team) {
        return new Servers(
                this.servers.values().stream()
                        .filter(server -> nonNull(team) && team.equals(server.getServerConfigs().team()))
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
                        .filter(server -> nonNull(server.getServerConfigs().springActuatorBasicAuthenticationConfigs()))
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

    @SneakyThrows
    public final Servers reloadServers() {
        this.servers = this.readComputedServers();
        return this.getServers();
    }

    public final boolean isAnyChanges() {
        return this.getServers().isAnyChanges();
    }

    public final ServerInfinityTimerTask getComputedServer(Integer serverId) {
        return this.servers.values().stream()
                .filter(server -> nonNull(serverId) && serverId.equals(server.getId()))
                .findFirst()
                .orElseThrow(() -> new ServerNotFoundException(serverId));
    }

    public final ServerInfinityTimerTask getComputedServer(Integer serverId, Team team) {
        return this.servers.values().stream()
                .filter(server -> nonNull(team) && team.equals(server.getServerConfigs().team()))
                .filter(server -> nonNull(serverId) && serverId.equals(server.getId()))
                .findFirst()
                .orElseThrow(() -> new ServerNotFoundException(serverId));
    }

    public OpsIncident getOpsIncident(Incident incident, OpsIncidentEnv opsIncidentEnv) {
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
                        serverIpAddress,
                        server.getIncidentsNotificationsMetadata()
                );

                serversMappedByTeams.put(serverIpAddressURI.getHost(), serverMin);
                var aliases = server.getServerConfigs().aliases();
                if (!isEmpty(aliases)) {
                    aliases.forEach(alias -> serversMappedByTeams.put(alias, serverMin));
                }
            }
        });
        var server = serversMappedByTeams.getOrDefault(remoteHost, ServerMin.unexpected(opsIncidentEnv));
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
    private ComputedServers readComputedServers() throws IOException {
        var gc = this.opsProperties.getGithubConfigs();

        var configuration = createTempFile("github-", "-contents");
        configuration.deleteOnExit();
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

        System.out.println("---");
        System.out.println(json);
        System.out.println("---");

        var opsConfigs = this.objectMapper.readValue(json, OpsConfigs.class);

        LOGGER.info("Read GitHub configuration, filtering servers: `{}`. Status: `{}`", opsConfigs.getServersCount(), STARTED);
        opsConfigs.serversConfigs().removeIf(ServerConfigs::disableMonitoring);
        LOGGER.info("Read GitHub configuration, filtering servers: `{}`. Status: `{}`", opsConfigs.getServersCount(), COMPLETED);

        if (opsConfigs.isAnyUnexpectedServersTeams()) {
            this.applicationEventPublisher.publishEvent(opsConfigs.getIncidentUnexpectedTeams());
        }

        if (opsConfigs.isAnyUnexpectedSshKeys()) {
            this.applicationEventPublisher.publishEvent(opsConfigs.getIncidentUnexpectedSshKeys());
        }

        return new ComputedServers(
                opsConfigs.serversConfigs().stream()
                        .map(serverConfigs ->
                                new ServerInfinityTimerTask(
                                        serverConfigs,
                                        this.opsProperties.getServersMonitoringConfigs(),
                                        this.serverInfinityTimerTaskSpringBeans,
                                        gc.getRsaKeysBaseLocation(),
                                        opsConfigs.getMappedSshKeys(),
                                        opsConfigs.getMappedTeamMembers()
                                )
                        ).collect(Collectors.toList())
        );
    }
}
