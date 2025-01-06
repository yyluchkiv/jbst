package jbst.ops.server.slack.services.options;

import jbst.ops.server.domain.slack.requests.SlackRequestContext;

public interface OptionsService {
    void sendMessagesBy(SlackRequestContext slackRequestContext);
    void sendFallbackMessage(SlackRequestContext slackRequestContext);
}
