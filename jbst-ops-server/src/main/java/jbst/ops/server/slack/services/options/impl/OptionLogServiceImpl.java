package jbst.ops.server.slack.services.options.impl;

import feign.Response;
import jbst.ops.server.domain.servers.Team;
import jbst.ops.server.domain.slack.requests.SlackRequestContext;
import jbst.ops.server.domain.storage.SupportedFormat;
import jbst.ops.server.exceptions.ServerNotFoundException;
import jbst.ops.server.exceptions.SlackRuntimeException;
import jbst.ops.server.properties.OpsProperties;
import jbst.ops.server.services.IncidentsProcessor;
import jbst.ops.server.services.LogsService;
import jbst.ops.server.services.StorageService;
import jbst.ops.server.slack.messaging.SlackMessagingService;
import jbst.ops.server.slack.services.options.OptionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;
import static jbst.foundation.utilities.slack.SlackUtility.getSlackMessage;
import static jbst.ops.server.domain.slack.teams.SlackTeamEvent.channelSlackMessage;

@SuppressWarnings({"DataFlowIssue", "deprecation"})
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OptionLogServiceImpl implements OptionLogService {

    // Service
    private final IncidentsProcessor incidentsProcessor;
    private final LogsService logsService;
    private final StorageService storageService;
    // Messaging
    private final SlackMessagingService slackMessagingService;
    // Properties
    private final OpsProperties opsProperties;

    @Override
    public void logs(SlackRequestContext slackRequestContext) {
        var serverId = slackRequestContext.getSlackKeywords().getServerIdIfPresent();
        try {
            this.getLogsMessageByResponse(
                    slackRequestContext,
                    this.logsService.attachArchivedLogs(serverId, null),
                    serverId
            );
        } catch (IOException ex) {
            this.incidentsProcessor.processIncident(ex);
            throw new ServerNotFoundException(serverId);
        }
    }

    @Override
    public void logs(SlackRequestContext slackRequestContext, Team team) {
        var serverId = slackRequestContext.getSlackKeywords().getServerIdIfPresent();
        try {
            this.getLogsMessageByResponse(
                    slackRequestContext,
                    this.logsService.attachArchivedLogs(serverId, team, null),
                    serverId
            );
        } catch (IOException ex) {
            this.incidentsProcessor.processIncident(ex);
            throw new ServerNotFoundException(serverId, team);
        }
    }

    // ================================================================================================================
    // Private Methods
    // ================================================================================================================
    private void getLogsMessageByResponse(SlackRequestContext slackRequestContext, Response response, Integer serverId) throws IOException {
        int status = response.status();
        if (HttpStatus.OK.value() == status) {
            var downloadURL = "%s/api/storage/%s?accessCode=%s".formatted(
                    this.opsProperties.getServerConfigs().getBaseURL(),
                    SupportedFormat.ZIP.getValue(),
                    this.storageService.saveStream(response.body().asInputStream()).value()
            );
            this.slackMessagingService.send(
                    channelSlackMessage(
                            slackRequestContext,
                            getSlackMessage(downloadURL)
                    )
            );
        } else {
            var trace = new BufferedReader(new InputStreamReader(response.body().asInputStream(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining(NEWLINE));
            throw new SlackRuntimeException("Logs response is not OK on monitoring-service: `{" + serverId + "}`. Trace: `" + trace + "`");
        }
    }
}
