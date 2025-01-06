package jbst.ops.server.properties.configs;

import jbst.ops.server.properties.atomics.SlackMainChannelCommunication;

public interface SlackConfigs {
    boolean isDisabled();
    String getBotToken();
    String getAppToken();
    SlackMainChannelCommunication getCommunication();
}
