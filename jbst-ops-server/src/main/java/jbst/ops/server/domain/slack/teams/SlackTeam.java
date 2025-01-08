package jbst.ops.server.domain.slack.teams;

@Deprecated
public enum SlackTeam {
    TECH1,
    SMART_APPS;

    public boolean isTech1() {
        return TECH1.equals(this);
    }

    public boolean isSmartApps() {
        return SMART_APPS.equals(this);
    }
}
