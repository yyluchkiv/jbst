package jbst.ops.server.slack.services.options;

import jbst.ops.server.domain.servers.Team;
import jbst.ops.server.domain.slack.requests.SlackRequestContext;

public interface OptionLogService {
    void logs(SlackRequestContext slackRequestContext);
    void logs(SlackRequestContext slackRequestContext, Team team);
}
