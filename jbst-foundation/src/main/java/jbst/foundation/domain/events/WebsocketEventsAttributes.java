package jbst.foundation.domain.events;

import lombok.experimental.UtilityClass;

@UtilityClass
public class WebsocketEventsAttributes {
    @UtilityClass
    public static class Keys {
        public static final String TYPE = "eventType";
    }
    @UtilityClass
    public static class Values {
        public static final String TYPE_HARDWARE_MONITORING = "HARDWARE_MONITORING";
        public static final String TYPE_RESET_SERVER_PROGRESS = "RESET_SERVER_PROGRESS";
    }
}
