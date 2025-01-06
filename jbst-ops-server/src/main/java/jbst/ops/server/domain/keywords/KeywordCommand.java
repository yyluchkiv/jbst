package jbst.ops.server.domain.keywords;

public enum KeywordCommand {
    // common (right now logs-service only)
    BY_ID,

    // gateway-service
    HELP, STATUS,

    // monitoring-service
    SHOW, ACTUATORS, FS, RELOAD;

    public boolean isId() {
        return BY_ID.equals(this);
    }
}
