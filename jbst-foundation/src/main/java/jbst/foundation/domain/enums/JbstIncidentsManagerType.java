package jbst.foundation.domain.enums;

import static jbst.foundation.domain.random.JbstRandom.randomEnum;

public enum JbstIncidentsManagerType {
    TELEGRAM;

    public static JbstIncidentsManagerType fixed() {
        return TELEGRAM;
    }

    public static JbstIncidentsManagerType random() {
        return randomEnum(JbstIncidentsManagerType.class);
    }

    public boolean isTelegram() {
        return this == TELEGRAM;
    }
}
