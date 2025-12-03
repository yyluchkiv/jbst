package jbst.foundation.domain.enums;

import static jbst.foundation.domain.random.JbstRandom.randomEnum;

public enum JbstIncidentsManagerType {
    SERVER;

    public static JbstIncidentsManagerType hardcoded() {
        return SERVER;
    }

    public static JbstIncidentsManagerType random() {
        return randomEnum(JbstIncidentsManagerType.class);
    }
}
