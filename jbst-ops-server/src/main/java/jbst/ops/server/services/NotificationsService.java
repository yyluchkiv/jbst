package jbst.ops.server.services;

import jbst.ops.server.domain.incidents.OpsIncident;
import jbst.ops.server.domain.servers.Servers;
import jbst.ops.server.domain.slack.messages.SlackMessageType;
import jbst.ops.server.properties.OpsProperties;
import jbst.ops.server.slack.SlackMessagingService;
import jbst.ops.server.slack.services.options.OptionFileSystemService;
import jbst.ops.server.slack.services.options.OptionMonitoringService;
import jbst.ops.server.utils.MessagesUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static jbst.ops.server.domain.slack.requests.SlackRequestContext.limitedSmartApps;
import static jbst.ops.server.domain.slack.requests.SlackRequestContext.limitedTech1;
import static jbst.ops.server.domain.slack.teams.SlackTeamEvent.communicationMainSlackIncident;
import static jbst.ops.server.domain.slack.teams.SlackTeamEvent.communicationTeamSlackIncident;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotificationsService {

    // Services
    private final OptionFileSystemService optionFileSystemService;
    private final OptionMonitoringService optionMonitoringService;
    // Messaging
    private final SlackMessagingService slackMessagingService;
    // Utilities
    private final MessagesUtils messagesUtils;
    // Properties
    private final OpsProperties opsProperties;

    public final void notifyShow(Servers servers) {
        this.optionMonitoringService.sendShowShortOrFailures(
                limitedTech1(),
                servers
        );
    }

    public final void notifyShowTeams(Servers servers) {
        var tcs = this.opsProperties.getTech1SlackConfigs().getTeamsCommunications();
        tcs.stream()
                .filter(tcc -> tcc.getCommunication().isEnabled())
                .filter(tcc -> servers.isAnyChanges(tcc.getTeam()))
                .forEach(tcc -> {
                    var team = tcc.getTeam();

                    // parent slack - Tech1: `any` team
                    this.optionMonitoringService.sendShowShortOrFailures(
                            limitedTech1(),
                            servers,
                            tcc
                    );

                    // child slack - SmartApps: SmartApps team
                    if (team.isSmartApps()) {
                        this.optionMonitoringService.sendShowShortOrFailures(
                                limitedSmartApps(),
                                servers
                        );
                    }
                });
    }

    public final void notifyFs(Servers servers) {
        this.optionFileSystemService.sendFsStatusOrFailures(
                limitedTech1(),
                servers,
                SlackMessageType.COMMUNICATION_MAIN
        );
    }

    public final void notifyFsFailures(Servers servers) {
        this.optionFileSystemService.sendFsFailures(
                limitedTech1(),
                servers,
                SlackMessageType.COMMUNICATION_MAIN
        );
    }

    public final void notifyShowChanges(Servers servers) {
        this.optionMonitoringService.sendChanges(
                limitedTech1(),
                servers
        );
    }

    public final void notifyIncident(OpsIncident opsIncident) {
        var incidentTuple = this.messagesUtils.getIncidentTuple(opsIncident);
        if (opsIncident.getServer().team().isTech1()) {
            this.slackMessagingService.sendAsync(
                    communicationMainSlackIncident(
                            limitedTech1(),
                            incidentTuple
                    )
            );
        } else {
            var tcs = this.opsProperties.getTech1SlackConfigs().getTeamsCommunications();
            tcs.stream()
                    .filter(tcc -> tcc.getCommunication().isEnabled())
                    .filter(tcc -> opsIncident.getServer().team().equals(tcc.getTeam()))
                    .forEach(tcc ->
                            this.slackMessagingService.sendAsync(
                                    communicationTeamSlackIncident(
                                            limitedTech1(),
                                            incidentTuple,
                                            tcc
                                    )
                            )
                    );
        }
    }
}
