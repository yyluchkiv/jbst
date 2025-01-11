package jbst.ops.server.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OpsConstants {
    public static class Services {
        // service-as-server
        public static final String MONITORING_SERVICE = "monitoring-service";

        // service-as-sub-service
        public static final String FILE_SYSTEM_SERVICE = "file-system-service";
        public static final String MONITORING_HISTORY_SERVICE = "monitoring-history-service";
        public static final String SPRING_BOOT_ACTUATOR_SERVICE = "spring-boot-actuator-service";
    }

    public static class Logs {
        public static final String PREFIX = "[Ops]";
    }
}
