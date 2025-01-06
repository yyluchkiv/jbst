package jbst.ops.server.slack.messaging;

import jbst.ops.server.domain.slack.teams.SlackTeamEvent;

import java.util.List;

public interface SlackMessagingService {
    void configure();
    void send(SlackTeamEvent slackTeamEvent);
    void send(List<SlackTeamEvent> slackTeamEvents);
}
