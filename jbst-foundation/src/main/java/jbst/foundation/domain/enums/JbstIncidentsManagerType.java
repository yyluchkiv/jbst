package jbst.foundation.domain.enums;

import static jbst.foundation.domain.random.JbstRandom.randomEnum;

public enum JbstIncidentsManagerType {
    SERVER,
    TELEGRAM;

    public static JbstIncidentsManagerType hardcoded() {
        return SERVER;
    }

    public static JbstIncidentsManagerType random() {
        return randomEnum(JbstIncidentsManagerType.class);
    }

    public boolean isServer() {
        return this == SERVER;
    }

    public boolean isTelegram() {
        return this == TELEGRAM;
    }
}
