package jbst.ops.server.domain.slack.teams;

import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;
import jbst.ops.server.properties.base.SlackConfigs;

public record SlackTeamEventContext(
        SlackConfigs configs,
        String channel,
        String message
) {

    public static SlackTeamEventContext direct(SlackConfigs configs, MessageEvent event, String message) {
        return new SlackTeamEventContext(configs, event.getChannel(), message);
    }

    public static SlackTeamEventContext channel(SlackConfigs configs, AppMentionEvent event, String message) {
        return new SlackTeamEventContext(configs, event.getChannel(), message);
    }
}
