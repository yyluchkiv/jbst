package jbst.ops.server.slack.slacks;

import jbst.ops.server.domain.slack.teams.SlackTeam;
import jbst.ops.server.exceptions.SlackInitializationException;
import jbst.ops.server.properties.OpsProperties;
import jbst.ops.server.slack.SlackMessagingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class SlackBotSmartApps extends SlackBot {

    @Autowired
    public SlackBotSmartApps(
            SlackMessagingService slackMessagingService,
            OpsProperties opsProperties
    ) throws SlackInitializationException {
        super(
                SlackTeam.SMART_APPS,
                opsProperties.getSmartAppsSlackConfigs(),
                slackMessagingService
        );
//        this.configure();
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
