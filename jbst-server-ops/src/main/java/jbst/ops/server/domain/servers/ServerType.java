package jbst.ops.server.domain.servers;

public enum ServerType {
    SERVER_AS_PING,
    SERVER_AS_FULL_SPRING_BOOT;

    public boolean isServerPing() {
        return SERVER_AS_PING.equals(this);
    }

    public boolean isServerSpringBoot() {
        return SERVER_AS_FULL_SPRING_BOOT.equals(this);
    }
}
