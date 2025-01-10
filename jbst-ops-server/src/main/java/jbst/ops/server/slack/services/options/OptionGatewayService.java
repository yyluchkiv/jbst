package jbst.ops.server.slack.services.options;

import jbst.ops.server.domain.slack.requests.SlackRequestContext;

public interface OptionGatewayService {
    void sendGatewayStatus(SlackRequestContext slackRequestContext);
    void sendSpringBootActuatorsGatewayStatus(SlackRequestContext slackRequestContext);
    void sendFsGatewayStatus(SlackRequestContext slackRequestContext);
}
