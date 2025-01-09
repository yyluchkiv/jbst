package jbst.ops.server.slack.slacks;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.socket_mode.SocketModeApp;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;
import jbst.ops.server.properties.configs.SlackConfigs;
import jbst.ops.server.slack.SlackClient;
import jbst.ops.server.slack.SlackMessagingService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Getter
public class SlackBot {
    // Configs
    private final SlackConfigs configs;
    // Services
    private final SlackMessagingService slackMessagingService;
    // Clients
    private final SlackClient slackClient;

    public final void initialize() {
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
            LOGGER.error("Slack initialization failure. Configs: {}", this.configs, ex);
            throw new RuntimeException(ex);
        }
    }

    public final void onDirectMessagePosted(EventsApiPayload<MessageEvent> payload) {
        System.out.println("======");
        System.out.println(payload.getEvent().getText());
        System.out.println(payload.getEvent().getTeam());
        System.out.println("======");
    }

    // TODO [YYL] reuse/fixme
//    public final void onDirectMessagePosted(EventsApiPayload<MessageEvent> payload) {
//        var slackContext = new SlackRequestContext(this.team, payload.getEvent());
//        this.slackMessagingService.sendAsync(
//                directSlackMessage(
//                        slackContext,
//                        getReadOnlyWarning()
//                )
//        );
//    }

    public final void onMentionedMessagePosted(EventsApiPayload<AppMentionEvent> payload) {
        // TODO [YYL] add code
    }
}
