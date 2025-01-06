package jbst.ops.server.slack.services.options;

import jbst.ops.server.domain.servers.Servers;
import jbst.ops.server.domain.servers.Team;
import jbst.ops.server.domain.slack.requests.SlackRequestContext;
import jbst.ops.server.properties.atomics.SlackTeamChannelCommunication;

public interface OptionMonitoringService {
    void sendShow(SlackRequestContext slackRequestContext);
    void sendShow(SlackRequestContext slackRequestContext, Team team);
    void sendSpringBootActuators(SlackRequestContext slackRequestContext);
    void sendReload(SlackRequestContext slackRequestContext);

    void sendChanges(SlackRequestContext slackRequestContext, Servers servers);
    void sendShowShortOrFailures(SlackRequestContext slackRequestContext, Servers servers);
    void sendShowShortOrFailures(SlackRequestContext slackRequestContext, Servers servers, SlackTeamChannelCommunication slackTeamChannelCommunication);
}
