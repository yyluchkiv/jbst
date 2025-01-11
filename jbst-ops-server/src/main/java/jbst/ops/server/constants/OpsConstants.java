package jbst.ops.server.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OpsConstants {
    public static class Services {
        public static final String STATUS_SERVICE = "servers-status-service";
        public static final String FILE_SYSTEM_SERVICE = "servers-file-system-service";
        public static final String HISTORY_SERVICE = "servers-history-service";
        public static final String SPRING_BOOT_ACTUATORS_SERVICE = "spring-boot-actuator-service";
    }

    public static class Logs {
        public static final String PREFIX = "[Ops]";
    }
}
