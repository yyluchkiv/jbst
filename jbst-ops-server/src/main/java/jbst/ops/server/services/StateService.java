package jbst.ops.server.services;

import jbst.ops.server.domain.slack.requests.SlackRequestContext;
import jbst.ops.server.slack.SlackMessagingService;
import jbst.ops.server.utilities.MessagesUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

import static jbst.ops.server.domain.slack.requests.SlackRequestContext.limitedTech1;
import static jbst.ops.server.domain.slack.teams.SlackTeamEventV1.communicationMainSlackMessage;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StateService {

    private final AtomicBoolean configured = new AtomicBoolean(false);

    // Services
    private final MonitoringService monitoringService;
    // Messaging
    private final SlackMessagingService slackMessagingService;

    public final boolean assertConfiguredCheck(SlackRequestContext slackRequestContext) {
        var monitoringServerConfigured = this.monitoringService.isConfigured();
        var state = this.configured.get();

        // CASE: monitoring-service is NOT configured, previous state == false
        if (!monitoringServerConfigured && !state) {
            this.sendBotNotConfiguredYet(slackRequestContext);
        }

        // CASE: monitoring-service is NOT configured, previous state == true
        if (!monitoringServerConfigured && state) {
            this.configured.set(false);
            this.sendBotNotConfiguredYet(slackRequestContext);
        }

        // CASE: monitoring-service is configured, previous state == false
        if (monitoringServerConfigured && !state) {
            this.configured.set(true);
            this.sendBotConfigured();
        }

        // CASE: monitoring-service is configured, previous state == true - IGNORED, already bot configured

        return monitoringServerConfigured;
    }

    public final void configure() {
        var state = this.configured.get();
        if (!state) {
            this.configured.set(true);
            this.sendBotConfigured();
        }
    }

    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    public void sendBotNotConfiguredYet(SlackRequestContext slackRequestContext) {
        this.slackMessagingService.sendAsync(
                communicationMainSlackMessage(
                        slackRequestContext,
                        MessagesUtility.getBotNotConfiguredYet()
                )
        );
    }

    public void sendBotConfigured() {
        this.slackMessagingService.sendAsync(
                communicationMainSlackMessage(
                        limitedTech1(),
                        MessagesUtility.getBotConfigured()
                )
        );
    }
}
