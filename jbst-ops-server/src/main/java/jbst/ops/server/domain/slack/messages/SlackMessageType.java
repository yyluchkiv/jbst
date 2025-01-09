package jbst.ops.server.domain.slack.messages;

public enum SlackMessageType {
    CHANNEL,
    COMMUNICATION_MAIN,
    COMMUNICATION_TEAM;

    public boolean isCommunicationMain() {
        return COMMUNICATION_MAIN.equals(this);
    }

    public boolean isCommunicationTeam() {
        return COMMUNICATION_TEAM.equals(this);
    }
}
