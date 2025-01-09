package jbst.ops.server.domain.slack.bots;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.socket_mode.SocketModeApp;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.files.FilesUploadRequest;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;
import jbst.ops.server.domain.slack.commands.SlackRequestCommand;
import jbst.ops.server.properties.base.SlackConfigs;
import jbst.ops.server.slack.SlackMessagingService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

import static jbst.ops.server.utilities.MessagesUtility.getReadOnlyWarning;

@Slf4j
public record SlackBot(
        SlackConfigs configs,
        SlackMessagingService slackMessagingService,
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

    public void onDirectMessagePosted(EventsApiPayload<MessageEvent> payload) {
        this.sendMessage(getReadOnlyWarning(), payload.getEvent().getChannel());
    }

    public void onMentionedMessagePosted(EventsApiPayload<AppMentionEvent> payload) {
        System.out.println("-----");
        System.out.println("A: " + this.configs.isCommunicationReadOnly());
        System.out.println("B: " + !this.configs.getMainChannel().equals(payload.getEvent().getChannel()));
        System.out.println("-----");
        if (this.configs.isCommunicationReadOnly() || !this.configs.getMainChannel().equals(payload.getEvent().getChannel())) {
            this.sendMessage(getReadOnlyWarning(), payload.getEvent().getChannel());
            return;
        }
        var slackRequestCommand = new SlackRequestCommand(payload.getEvent());
        System.out.println("slackRequestCommand: " + slackRequestCommand);
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
