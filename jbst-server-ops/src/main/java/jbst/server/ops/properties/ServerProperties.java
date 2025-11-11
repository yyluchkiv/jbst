package jbst.server.ops.properties;

import jbst.foundation.domain.properties.base.JbstPropertyRemoteServer;
import jbst.foundation.domain.properties.base.JbstPropertySpringLogging;
import jbst.server.ops.domain.incidents.OpsIncidentEnv;
import jbst.server.ops.properties.configs.JbstPropertyOpsRecipients;
import jbst.server.ops.properties.configs.JbstPropertyOpsServers;
import jbst.server.ops.properties.configs.JbstPropertyOpsSlacks;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

@ConfigurationProperties(
        prefix = "jbst-server-ops",
        ignoreUnknownFields = false
)
@Data
public class ServerProperties implements PriorityOrdered {
    private JbstPropertySpringLogging logging;
    // WARNING: create new class to avoid using dedicated leaf(s) as root-based property
    private JbstPropertyRemoteServer server;
    private JbstPropertyOpsSlacks slacks;
    private JbstPropertyOpsServers servers;
    // WARNING: create new class to avoid using dedicated leaf(s) as root-based property
    private JbstPropertyOpsRecipients recipients;

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    public OpsIncidentEnv getOpsIncidentEnv() {
        return OpsIncidentEnv.of(this.server);
    }
}
