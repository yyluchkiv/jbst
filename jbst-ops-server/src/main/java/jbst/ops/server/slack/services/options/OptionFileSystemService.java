package jbst.ops.server.slack.services.options;

import jbst.ops.server.domain.servers.Servers;
import jbst.ops.server.domain.slack.messages.SlackMessageType;
import jbst.ops.server.domain.slack.requests.SlackRequestContext;

public interface OptionFileSystemService {
    void sendFsStatusOnSshRequiredAnyProblemsOnFsMetadata(SlackRequestContext slackRequestContext);
    void sendFsStatusOrFailures(SlackRequestContext slackRequestContext, Servers servers, SlackMessageType slackMessageType);
    void sendFsFailures(SlackRequestContext slackRequestContext, Servers servers, SlackMessageType slackMessageType);
}
