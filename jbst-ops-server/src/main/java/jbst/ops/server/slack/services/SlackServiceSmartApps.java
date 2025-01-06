package jbst.ops.server.slack.services;

import jbst.ops.server.domain.slack.teams.SlackTeam;
import jbst.ops.server.properties.OpsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SlackServiceSmartApps extends AbstractSlackService {

    @Autowired
    public SlackServiceSmartApps(OpsProperties opsProperties) {
        super(
                SlackTeam.SMART_APPS,
                opsProperties.getSmartAppsSlackConfigs()
        );
    }
}
