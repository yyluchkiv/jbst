package jbst.ops.server.domain.slack.bots;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.socket_mode.SocketModeApp;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.conversations.ConversationsInfoRequest;
import com.slack.api.methods.request.files.FilesUploadRequest;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;
import jbst.ops.server.domain.servers.Team;
import jbst.ops.server.properties.base.SlackConfigs;
import jbst.ops.server.properties.base.SlackTeamCommunication;
import jbst.ops.server.slack.SlackCommandsService;
import jbst.ops.server.utilities.MessagesUtility;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
public record SlackBot(
        SlackConfigs configs,
        SlackCommandsService slackCommandsService,
        MethodsClient methodsClient
) {

    public void initialize() {
        try {
            var app = new App(
                    AppConfig.builder()
                            .singleTeamBotToken(this.configs.getBotToken())
                            .build()
            );

            var socketModeApp = new SocketModeApp(this.configs.getAppToken(), app);
            socketModeApp.startAsync();

            socketModeApp.getApp().event(AppMentionEvent.class, (req, ctx) -> {
                this.onMentionedMessagePosted(req);
                return ctx.ack();
            });

            socketModeApp.getApp().event(MessageEvent.class, (req, ctx) -> {
                this.onDirectMessagePosted(req);
                return ctx.ack();
            });
        } catch (Exception ex) {
            LOGGER.error("Slack initialization failure. Configs: {}", this.configs, ex);
            throw new RuntimeException(ex);
        }
    }

    public void sendMainCommunication(String message) {
        this.sendMessage(message, this.configs.getMainCommunication());
    }

    public void sendMainCommunication(List<String> messages) {
        messages.forEach(this::sendMainCommunication);
    }

    public void sendTeamCommunication(String message, Team team) {
        var stcOpt = this.configs.getTeamCommunication(team);
        stcOpt.ifPresent(stc -> {
            if (stc.isOperationalMode()) {
                this.sendMessage(message, stc.getName());
            }
        });
    }

    // ================================================================================================================
    // PRIVATE METHODS: onEvents
    // ================================================================================================================
    private void onDirectMessagePosted(EventsApiPayload<MessageEvent> payload) {
        if ("im".equals(payload.getEvent().getChannelType())) {
            this.sendMessage(MessagesUtility.getReadOnlyWarning(), payload.getEvent().getChannel());
        }
    }

    private void onMentionedMessagePosted(EventsApiPayload<AppMentionEvent> payload) {
        // READONLY: communication-mode scenario
        if (this.configs.isReadOnlyMode()) {
            this.sendMessage(MessagesUtility.getReadOnlyWarning(), payload.getEvent().getChannel());
            return;
        }
        // READONLY: main-channel scenario
        try {
            var conversationsInfo = this.methodsClient.conversationsInfo(
                    ConversationsInfoRequest.builder()
                            .channel(payload.getEvent().getChannel())
                            .build()
            );
            if (!this.configs.getMainCommunication().equals(conversationsInfo.getChannel().getName())) {
                this.sendMessage(MessagesUtility.getReadOnlyWarning(), payload.getEvent().getChannel());
                return;
            }
        } catch (IOException | SlackApiException ex) {
            this.sendMessage(MessagesUtility.getReadOnlyWarning(), payload.getEvent().getChannel());
            return;
        }
        // HELP: invalid scenario
        var slackRequestCommand = new SlackRequest(payload.getEvent());
        if (!slackRequestCommand.isValid()) {
            this.sendMessage(MessagesUtility.getHelpTableHeader(), payload.getEvent().getChannel());
            this.sendMessage(SlackCommand.getHelpTable(), payload.getEvent().getChannel());
            return;
        }
        // PRODUCTION: "ops $cmd" scenario
        this.sendMessage(MessagesUtility.getExpensiveOperationStartedMessage(), payload.getEvent().getChannel());
        var messages = this.slackCommandsService.getMessages(slackRequestCommand);
        messages.forEach(message -> this.sendMessage(message, payload.getEvent().getChannel()));
        this.sendMessage(MessagesUtility.getExpensiveOperationCompletedMessage(), payload.getEvent().getChannel());
    }

    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    private void sendMessage(String message, String channel) {
        try {
            this.methodsClient.chatPostMessage(
                    ChatPostMessageRequest.builder()
                            .text(message)
                            .channel(channel)
                            .build()
            );
        } catch (SlackApiException | IOException ex) {
            // ignore
        }
    }

    @SuppressWarnings("unused")
    private void sendFile(String fileContent, String channel) {
        try {
            this.methodsClient.filesUpload(
                    FilesUploadRequest.builder()
                            .filename("incident-trace")
                            .content(fileContent)
                            .channels(List.of(channel))
                            .build()
            );
        } catch (SlackApiException | IOException ex) {
            // ignore
        }
    }
}
