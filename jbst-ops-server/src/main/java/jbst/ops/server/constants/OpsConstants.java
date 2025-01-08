package jbst.ops.server.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OpsConstants {
    public static class Services {
        // service-as-server
        public static final String GATEWAY_SERVICE = "gateway-service";
        public static final String MONITORING_SERVICE = "monitoring-service";

        // service-as-sub-service
        public static final String FILE_SYSTEM_SERVICE = "file-system-service";
        public static final String MONITORING_HISTORY_SERVICE = "monitoring-history-service";
        public static final String SPRING_BOOT_ACTUATOR_SERVICE = "spring-boot-actuator-service";
    }

    @Deprecated
    public static class Teams {
        public static final String TECH1 = "TECH1";
        // TODO [YYL] SMART_APPS -> SMART_TRADER
        public static final String SMART_APPS = "SMART_APPS";
    }

    public static class Logs {
        public static final String PREFIX = "[Ops]";
    }
}
