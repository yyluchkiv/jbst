package jbst.ops.server.domain.slack.messages;

public enum SlackMessageType {
    DIRECT,
    CHANNEL,
    COMMUNICATION_MAIN,
    COMMUNICATION_TEAM;

    public boolean isDirect() {
        return DIRECT.equals(this);
    }

    public boolean isChannel() {
        return CHANNEL.equals(this);
    }

    public boolean isCommunicationMain() {
        return COMMUNICATION_MAIN.equals(this);
    }

    public boolean isCommunicationTeam() {
        return COMMUNICATION_TEAM.equals(this);
    }

    public boolean isDirectOrChannel() {
        return this.isDirect() || this.isChannel();
    }
}
