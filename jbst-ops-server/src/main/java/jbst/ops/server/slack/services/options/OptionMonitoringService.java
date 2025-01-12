package jbst.ops.server.slack.services.options;

import jbst.ops.server.domain.servers.Servers;
import jbst.ops.server.domain.slack.requests.SlackRequestContext;

public interface OptionMonitoringService {
    void sendShowShortOrFailures(SlackRequestContext slackRequestContext, Servers servers);
}
