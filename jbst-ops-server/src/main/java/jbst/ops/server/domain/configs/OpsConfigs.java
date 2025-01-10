package jbst.ops.server.domain.configs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jbst.foundation.incidents.domain.Incident;
import jbst.foundation.incidents.domain.IncidentAttributes;
import jbst.ops.server.domain.configs.ssh.SshRsaKey;
import jbst.ops.server.domain.servers.Subcontractor;
import jbst.ops.server.domain.servers.SubcontractorId;
import jbst.ops.server.domain.servers.Team;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public record OpsConfigs(
        Set<Subcontractor> subcontractors,
        Set<SshRsaKey> sshKeys,
        Set<Team> teams,
        List<ServerConfigs> serversConfigs
) {
    @JsonIgnore
    public long getServersCount() {
        return this.serversConfigs.size();
    }

    @JsonIgnore
    public Map<String, SshRsaKey> getMappedSshKeys() {
        return this.sshKeys.stream().collect(Collectors.toMap(SshRsaKey::name, entry -> entry));
    }

    @JsonIgnore
    public Map<SubcontractorId, Subcontractor> getMappedTeamMembers() {
        return this.subcontractors.stream().collect(Collectors.toMap(Subcontractor::subcontractorId, entry -> entry));
    }

    @JsonIgnore
    public Set<Team> getServersTeams() {
        return this.serversConfigs.stream()
                .map(ServerConfigs::team)
                .collect(Collectors.toSet());
    }

    @JsonIgnore
    public Set<String> getServersSshKeys() {
        return this.serversConfigs.stream()
                .filter(server -> nonNull(server.sshConfigs()))
                .map(server -> server.sshConfigs().sshKey())
                .collect(Collectors.toSet());
    }

    @JsonIgnore
    public boolean isAnyUnexpectedServersTeams() {
        return !this.teams.containsAll(this.getServersTeams());
    }

    @JsonIgnore
    public boolean isAnyUnexpectedSshKeys() {
        return !this.getMappedSshKeys().keySet().containsAll(this.getServersSshKeys());
    }

    @JsonIgnore
    public Incident getIncidentUnexpectedTeams() {
        var incident = new Incident();
        incident.add(IncidentAttributes.Keys.TYPE, "Unexpected Teams");
        incident.add("Teams Configs", this.teams);
        incident.add("Teams Servers", this.getServersTeams());
        return incident;
    }

    @JsonIgnore
    public Incident getIncidentUnexpectedSshKeys() {
        var incident = new Incident();
        incident.add(IncidentAttributes.Keys.TYPE, "Unexpected SSH Keys");
        incident.add("SSH Keys Configs", this.getMappedSshKeys().keySet());
        incident.add("SSH Keys Servers", this.getServersSshKeys());
        return incident;
    }
}
