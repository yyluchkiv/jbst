package jbst.ops.server.slack.slacks;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.socket_mode.SocketModeApp;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;
import jbst.ops.server.domain.slack.requests.SlackRequestContext;
import jbst.ops.server.domain.slack.teams.SlackTeam;
import jbst.ops.server.exceptions.SlackInitializationException;
import jbst.ops.server.properties.configs.SlackConfigs;
import jbst.ops.server.slack.messaging.SlackMessagingService;
import jbst.ops.server.utils.MessagesUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static jbst.ops.server.domain.slack.teams.SlackTeamEvent.directSlackMessage;

@AllArgsConstructor
@Getter
public abstract class SlackApp {
    // Configs
    protected final SlackTeam team;
    protected final SlackConfigs configs;
    // Services
    protected final SlackMessagingService slackMessagingService;
    // Utils
    protected final MessagesUtils messagesUtils;

    public abstract void onMentionedMessagePosted(EventsApiPayload<AppMentionEvent> payload);

    public final void configure() throws SlackInitializationException {
        if (this.configs.isDisabled()) {
            return;
        }
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
            throw new SlackInitializationException(ex);
        }
    }

    public final void onDirectMessagePosted(EventsApiPayload<MessageEvent> payload) {
        var slackContext = new SlackRequestContext(this.team, payload.getEvent());
        this.slackMessagingService.send(
                directSlackMessage(
                        slackContext,
                        this.messagesUtils.getReadOnlyWarning()
                )
        );
    }
}
