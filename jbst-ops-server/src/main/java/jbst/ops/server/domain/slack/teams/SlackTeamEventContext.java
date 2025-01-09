package jbst.ops.server.domain.slack.teams;

import com.slack.api.model.event.MessageEvent;
import jbst.ops.server.properties.base.SlackConfigs;

public record SlackTeamEventContext(
        SlackConfigs configs,
        MessageEvent messageEvent,
        String message
) {
}
