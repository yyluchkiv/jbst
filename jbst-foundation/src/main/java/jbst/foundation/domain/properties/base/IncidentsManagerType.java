package jbst.foundation.domain.properties.base;

import static jbst.foundation.utilities.random.RandomUtility.randomEnum;

public enum IncidentsManagerType {
    SERVER;

    public static IncidentsManagerType hardcoded() {
        return SERVER;
    }

    public static IncidentsManagerType random() {
        return randomEnum(IncidentsManagerType.class);
    }
}
