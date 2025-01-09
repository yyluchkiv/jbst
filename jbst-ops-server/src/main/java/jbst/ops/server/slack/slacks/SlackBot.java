package jbst.ops.server.slack.slacks;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.socket_mode.SocketModeApp;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;
import jbst.ops.server.domain.slack.teams.SlackTeamEventContext;
import jbst.ops.server.slack.SlackClient;
import jbst.ops.server.slack.SlackMessagingService;
import lombok.extern.slf4j.Slf4j;

import static jbst.ops.server.utilities.MessagesUtility.getReadOnlyWarning;

@Slf4j
public record SlackBot(
        SlackMessagingService slackMessagingService,
        SlackClient slackClient
) {

    public void initialize() {
        try {
            var app = new App(
                    AppConfig.builder()
                            .singleTeamBotToken(this.slackClient.getConfigs().getBotToken())
                            .build()
            );

            var socketModeApp = new SocketModeApp(this.slackClient.getConfigs().getAppToken(), app);
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
            LOGGER.error("Slack initialization failure. Configs: {}", this.slackClient.getConfigs(), ex);
            throw new RuntimeException(ex);
        }
    }

    public void onDirectMessagePosted(EventsApiPayload<MessageEvent> payload) {
        this.slackClient.sendDirectOrChannel(
                new SlackTeamEventContext(
                        this.slackClient.getConfigs(),
                        payload.getEvent(),
                        getReadOnlyWarning()
                )
        );
    }

    public void onMentionedMessagePosted(EventsApiPayload<AppMentionEvent> payload) {
        // TODO [YYL] add code
    }
}
