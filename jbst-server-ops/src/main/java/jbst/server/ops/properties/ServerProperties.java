package jbst.server.ops.properties;

import jbst.foundation.domain.properties.base.RemoteServer;
import jbst.foundation.domain.properties.base.SpringLogging;
import jbst.server.ops.domain.incidents.OpsIncidentEnv;
import jbst.server.ops.properties.configs.RecipientsConfigs;
import jbst.server.ops.properties.configs.ServersConfigs;
import jbst.server.ops.properties.configs.SlacksConfigs;
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
    private SpringLogging logging;
    // WARNING: create new class to avoid using dedicated leaf(s) as root-based property
    private RemoteServer serverConfigs;
    private SlacksConfigs slacksConfigs;
    private ServersConfigs serversConfigs;
    // WARNING: create new class to avoid using dedicated leaf(s) as root-based property
    private RecipientsConfigs recipientsConfigs;

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    public OpsIncidentEnv getOpsIncidentEnv() {
        return OpsIncidentEnv.of(this.serverConfigs);
    }
}
