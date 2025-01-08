package jbst.ops.server.properties;

import jbst.foundation.domain.properties.base.RecipientsConfigs;
import jbst.foundation.domain.properties.base.RemoteServer;
import jbst.foundation.domain.properties.base.ScheduledJob;
import jbst.foundation.domain.properties.base.SpringLogging;
import jbst.ops.server.domain.incidents.OpsIncidentEnv;
import jbst.ops.server.properties.configs.*;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

@ConfigurationProperties(prefix = "ops-configs")
@Data
public class OpsProperties implements PriorityOrdered {
    private SpringLogging logging;
    private RemoteServer serverConfigs;
    private ServersConfigs serversConfigs;
    // TODO [YYL] clean: monitoring
    private CronsConfigs cronsConfigs;
    private ScheduledJob checkServersAnyChangesJobConfigs;
    private ServersMonitoringConfigs serversMonitoringConfigs;
    // TODO [YYL] clean: incidents
    private RecipientsConfigs recipientsConfigs;
    private ThrowableFiltrationConfigs throwableFiltrationConfigs;
    // TODO [YYL] clean: gateway
    private KeywordsConfigs keywordsConfigs;
    private MessagesConfigs messagesConfigs;
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
