package jbst.ops.server.slack.services.state;

import jbst.ops.server.domain.slack.requests.SlackRequestContext;

public interface StateService {
    boolean assertConfiguredCheck(SlackRequestContext slackRequestContext);
    void configure();
}
