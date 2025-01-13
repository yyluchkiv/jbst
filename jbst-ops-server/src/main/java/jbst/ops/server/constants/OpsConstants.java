package jbst.ops.server.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OpsConstants {
    public static class Tasks {
        public static final String STATUS_TASK = "servers-status-task";
        public static final String FILE_SYSTEM_SERVICE = "servers-file-system-task";
        public static final String HISTORY_TASK = "servers-history-task";
        public static final String SPRING_BOOT_ACTUATORS_TASK = "spring-boot-actuator-task";
    }

    public static class Logs {
        public static final String PREFIX = "[Ops]";
    }
}
