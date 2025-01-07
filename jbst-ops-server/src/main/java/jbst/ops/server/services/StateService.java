package jbst.ops.server.services;

import jbst.ops.server.domain.slack.requests.SlackRequestContext;
import jbst.ops.server.slack.messaging.SlackMessagingService;
import jbst.ops.server.utils.MessagesUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

import static jbst.ops.server.domain.slack.requests.SlackRequestContext.limitedTech1;
import static jbst.ops.server.domain.slack.teams.SlackTeamEvent.communicationMainSlackMessage;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StateService {

    private final AtomicBoolean atomicState = new AtomicBoolean(false);

    // Services
    private final MonitoringService monitoringService;
    // Messaging
    private final SlackMessagingService slackMessagingService;
    // Utilities
    private final MessagesUtils messagesUtils;

    public final boolean assertConfiguredCheck(SlackRequestContext slackRequestContext) {
        var monitoringServerConfigured = this.monitoringService.isConfigured();
        var state = this.atomicState.get();

        // CASE: monitoring-server is NOT configured, previous state == false
        if (!monitoringServerConfigured && !state) {
            this.sendBotNotConfiguredYet(slackRequestContext);
        }

        // CASE: monitoring-server is NOT configured, previous state == true
        if (!monitoringServerConfigured && state) {
            this.atomicState.set(false);
            this.sendBotNotConfiguredYet(slackRequestContext);
        }

        // CASE: monitoring-server is configured, previous state == false
        if (monitoringServerConfigured && !state) {
            this.atomicState.set(true);
            this.sendBotConfigured();
        }

        // CASE: monitoring-server is configured, previous state == true - IGNORED, already bot configured

        return monitoringServerConfigured;
    }

    public final void configure() {
        var state = this.atomicState.get();
        if (!state) {
            this.atomicState.set(true);
            this.sendBotConfigured();
        }
    }

    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    public void sendBotNotConfiguredYet(SlackRequestContext slackRequestContext) {
        this.slackMessagingService.send(
                communicationMainSlackMessage(
                        slackRequestContext,
                        this.messagesUtils.getBotNotConfiguredYet()
                )
        );
    }

    public void sendBotConfigured() {
        this.slackMessagingService.send(
                communicationMainSlackMessage(
                        limitedTech1(),
                        this.messagesUtils.getBotConfigured()
                )
        );
    }
}
