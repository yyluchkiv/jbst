package jbst.ops.server.properties;

import jbst.foundation.domain.properties.base.RecipientsConfigs;
import jbst.foundation.domain.properties.base.RemoteServer;
import jbst.foundation.domain.properties.base.SpringLogging;
import jbst.ops.server.domain.incidents.OpsIncidentEnv;
import jbst.ops.server.properties.configs.KeywordsConfigs;
import jbst.ops.server.properties.configs.ServersConfigs;
import jbst.ops.server.properties.configs.SmartAppsSlackConfigs;
import jbst.ops.server.properties.configs.Tech1SlackConfigs;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

@ConfigurationProperties(prefix = "ops")
@Data
public class OpsProperties implements PriorityOrdered {
    private SpringLogging logging;
    private RemoteServer serverConfigs;
    private ServersConfigs serversConfigs;
    private RecipientsConfigs recipientsConfigs;
    // TODO [YYL] clean: gateway
    private KeywordsConfigs keywordsConfigs;
    private Tech1SlackConfigs tech1SlackConfigs;
    private SmartAppsSlackConfigs smartAppsSlackConfigs;

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    public OpsIncidentEnv getOpsIncidentEnv() {
        return OpsIncidentEnv.of(this.serverConfigs);
    }
}
