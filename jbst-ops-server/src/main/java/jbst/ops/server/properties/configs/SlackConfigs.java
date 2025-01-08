package jbst.ops.server.properties.configs;

import jbst.ops.server.domain.servers.TeamV2;
import jbst.ops.server.properties.atomics.SlackMainChannelCommunication;

public interface SlackConfigs {
    boolean isDisabled();
    TeamV2 getTeam();
    String getBotToken();
    String getAppToken();
    SlackMainChannelCommunication getCommunication();
}
