package jbst.ops.server.slack.services.keywords;

import jbst.ops.server.domain.keywords.ServiceKeywordCommandKey;
import jbst.ops.server.domain.slack.requests.SlackRequestContext;

import java.util.Map;
import java.util.function.Consumer;

public interface KeywordsService {
    void sendMessagesBy(SlackRequestContext slackRequestContext, Map<ServiceKeywordCommandKey, Consumer<SlackRequestContext>> configs);
}
