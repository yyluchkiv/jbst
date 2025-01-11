package jbst.ops.server.services;

import jbst.ops.server.domain.incidents.OpsIncident;
import jbst.ops.server.domain.servers.Servers;
import jbst.ops.server.domain.slack.messages.SlackMessageType;
import jbst.ops.server.slack.SlackBotsService;
import jbst.ops.server.slack.services.options.OptionFileSystemService;
import jbst.ops.server.slack.services.options.OptionMonitoringService;
import jbst.ops.server.utilities.MessagesUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;
import static jbst.ops.server.constants.OpsConstants.Services.MONITORING_HISTORY_SERVICE;
import static jbst.ops.server.domain.slack.requests.SlackRequestContext.limitedTech1;
import static jbst.ops.server.utilities.MessagesUtility.getServiceHeaderMessage;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotificationsService {

    // Services
    private final SlackBotsService slackBotsService;
    private final OptionFileSystemService optionFileSystemService;

    public final void notifyShow(Servers servers) {
        if (servers.isAnyProblems()) {

        } else {

        }

    }

    // TODO [YYL] fixme
    public final void notifyShowTeams(Servers servers) {
//        var tcs = this.opsProperties.getTech1SlackConfigs().getTeamsCommunications();
//        tcs.stream()
//                .filter(tcc -> tcc.getCommunication().isEnabled())
//                .filter(tcc -> servers.isAnyChanges(tcc.getTeam()))
//                .forEach(tcc -> {
//                    var team = tcc.getTeam();
//
//                    // parent slack - Tech1: `any` team
//                    this.optionMonitoringService.sendShowShortOrFailures(
//                            limitedTech1(),
//                            servers,
//                            tcc
//                    );
//
//                    // child slack - SmartApps: SmartApps team
//                    if (team.isSmartApps()) {
//                        this.optionMonitoringService.sendShowShortOrFailures(
//                                limitedSmartApps(),
//                                servers
//                        );
//                    }
//                });
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
        if (servers.isAnyChanges()) {
            var serversHistory = servers.getValues().stream()
                    .map(server -> MessagesUtility.getServerHistoryMessage(server.name().value(), server.upHistory()))
                    .collect(Collectors.joining(NEWLINE));
            var message = getServiceHeaderMessage(MONITORING_HISTORY_SERVICE) + NEWLINE + serversHistory;
            this.slackBotsService.sendMainBotMainCommunication(List.of(message));
        }
    }

    // TODO [YYL] fixme: add "incident" flag
    public final void notifyIncident(OpsIncident opsIncident) {
//        var incidentTuple = MessagesUtility.getIncidentTuple(opsIncident);
//        if (opsIncident.getServer().team().isTech1()) {
//            this.slackMessagingService.sendAsync(
//                    communicationMainSlackIncident(
//                            limitedTech1(),
//                            incidentTuple
//                    )
//            );
//        } else {
//            var tcs = this.opsProperties.getTech1SlackConfigs().getTeamsCommunications();
//            tcs.stream()
//                    .filter(tcc -> tcc.getCommunication().isEnabled())
//                    .filter(tcc -> opsIncident.getServer().team().equals(tcc.getTeam()))
//                    .forEach(tcc ->
//                            this.slackMessagingService.sendAsync(
//                                    communicationTeamSlackIncident(
//                                            limitedTech1(),
//                                            incidentTuple,
//                                            tcc
//                                    )
//                            )
//                    );
//        }
    }
}
