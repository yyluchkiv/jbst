package jbst.ops.server.slack.slacks;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.model.event.AppMentionEvent;
import jbst.ops.server.domain.slack.requests.SlackRequestContext;
import jbst.ops.server.domain.slack.teams.SlackTeam;
import jbst.ops.server.exceptions.SlackInitializationException;
import jbst.ops.server.properties.OpsProperties;
import jbst.ops.server.slack.SlackMessagingService;
import jbst.ops.server.utils.MessagesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static jbst.ops.server.domain.slack.teams.SlackTeamEvent.channelSlackMessage;


@Slf4j
@Component
public class SlackBotSmartApps extends SlackBot {

    @Autowired
    public SlackBotSmartApps(
            SlackMessagingService slackMessagingService,
            MessagesUtils messagesUtils,
            OpsProperties opsProperties
    ) throws SlackInitializationException {
        super(
                SlackTeam.SMART_APPS,
                opsProperties.getSmartAppsSlackConfigs(),
                slackMessagingService,
                messagesUtils
        );
        this.configure();
    }

//    @Override
//    public void onMentionedMessagePosted(EventsApiPayload<AppMentionEvent> payload) {
//        var slackContext = new SlackRequestContext(this.team, payload.getEvent());
//        this.slackMessagingService.sendAsync(
//                channelSlackMessage(
//                        slackContext,
//                        this.messagesUtils.getReadOnlyWarning()
//                )
//        );
//    }
}
