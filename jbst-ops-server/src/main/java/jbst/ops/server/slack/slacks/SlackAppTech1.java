package jbst.ops.server.slack.slacks;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;
import jbst.ops.server.domain.slack.requests.SlackRequestContext;
import jbst.ops.server.domain.slack.teams.SlackTeam;
import jbst.ops.server.exceptions.SlackInitializationException;
import jbst.ops.server.properties.OpsProperties;
import jbst.ops.server.slack.messaging.SlackMessagingService;
import jbst.ops.server.slack.request.SlackRequestService;
import jbst.ops.server.slack.services.SlackServiceTech1;
import jbst.ops.server.slack.services.options.OptionsService;
import jbst.ops.server.slack.services.state.StateService;
import jbst.ops.server.utils.MessagesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static jbst.ops.server.domain.slack.teams.SlackTeamEvent.*;

@Slf4j
@Component
public class SlackAppTech1 extends SlackApp {

    // Slack
    private final SlackServiceTech1 slackServiceTech1;
    private final SlackRequestService slackRequestService;
    // State
    private final StateService stateService;
    // Messaging
    private final SlackMessagingService slackMessagingService;
    // Services
    private final OptionsService optionsService;
    // Utilities
    private final MessagesUtils messagesUtils;
    // Properties
    private final OpsProperties opsProperties;

    @Autowired
    public SlackAppTech1(
            SlackServiceTech1 slackServiceTech1,
            SlackRequestService slackRequestService,
            StateService stateService,
            SlackMessagingService slackMessagingService,
            OptionsService optionsService,
            MessagesUtils messagesUtils,
            OpsProperties opsProperties
    ) throws SlackInitializationException {
        super(
                SlackTeam.TECH1,
                opsProperties.getTech1SlackConfigs()
        );
        this.slackServiceTech1 = slackServiceTech1;
        this.slackRequestService = slackRequestService;
        this.stateService = stateService;
        this.slackMessagingService = slackMessagingService;
        this.optionsService = optionsService;
        this.messagesUtils = messagesUtils;
        this.opsProperties = opsProperties;
        this.configure();
    }

    @Override
    public void onDirectMessagePosted(EventsApiPayload<MessageEvent> payload) {
        var requestContext = new SlackRequestContext(this.team, payload.getEvent());
        this.slackMessagingService.send(
                directSlackMessage(
                        requestContext,
                        this.messagesUtils.getReadOnlyWarning()
                )
        );

        this.slackMessagingService.send(
                communicationMainSlackMessage(
                        requestContext,
                        this.messagesUtils.getUnfaithfulMessage(requestContext)
                )
        );
    }

    @Override
    public void onMentionedMessagePosted(EventsApiPayload<AppMentionEvent> payload) {
        var slackRequestContext = new SlackRequestContext(this.team, payload.getEvent());
        try {
            var slackKeywords = this.slackRequestService.getSlackKeywords(payload.getEvent());
            slackRequestContext.addSlackKeywords(
                    this.slackServiceTech1,
                    this.opsProperties.getTech1SlackConfigs(),
                    slackKeywords
            );
            this.sendProcessedMessage(slackRequestContext);
        } catch (SlackApiException | IOException | RuntimeException ex) {
            this.sendExceptionMessage(slackRequestContext, ex);
        }
    }

    // ================================================================================================================
    // Private Methods
    // ================================================================================================================
    private void sendProcessedMessage(SlackRequestContext slackRequestContext) {
        if (this.stateService.assertConfiguredCheck(slackRequestContext)) {
            LOGGER.debug("Process slack message. Permissions: `{}`", slackRequestContext.getPermissions());
            LOGGER.debug("Process slack message. Keywords: `{}`", slackRequestContext.getSlackKeywords());

            this.slackMessagingService.send(
                    channelSlackMessage(
                            slackRequestContext,
                            this.messagesUtils.getExpensiveOperationStartedMessage()
                    )
            );

            this.optionsService.sendMessagesBy(slackRequestContext);

            this.slackMessagingService.send(
                    channelSlackMessage(
                            slackRequestContext,
                            this.messagesUtils.getExpensiveOperationCompletedMessage()
                    )
            );
        }
    }

    private void sendExceptionMessage(SlackRequestContext slackRequestContext, Throwable throwable) {
        this.slackMessagingService.send(
                channelSlackMessage(
                        slackRequestContext,
                        this.messagesUtils.getUnexpectedWarning()
                )
        );

        this.optionsService.sendFallbackMessage(slackRequestContext);

        this.slackMessagingService.send(
                communicationMainSlackMessage(
                        slackRequestContext,
                        this.messagesUtils.getUnfaithfulMessage(slackRequestContext, throwable)
                )
        );
    }
}
