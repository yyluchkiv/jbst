package jbst.ops.server.domain.servers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.NotNull;

import static jbst.ops.server.constants.OpsConstants.Teams.SMART_APPS;
import static jbst.ops.server.constants.OpsConstants.Teams.TECH1;

public record Team(@NotNull String value) {
    @JsonCreator
    public static Team of(String value) {
        return new Team(value);
    }

    public static Team tech1() {
        return of(TECH1);
    }

    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }

    public boolean isTech1() {
        return TECH1.equals(this.value());
    }

    public boolean isSmartApps() {
        return SMART_APPS.equals(this.value());
    }
}
