package jbst.ops.server.slack.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jbst.ops.server.domain.slack.teams.SlackTeam;
import jbst.ops.server.properties.OpsProperties;

@Slf4j
@Service
public class SlackServiceTech1 extends SlackService {

    @Autowired
    public SlackServiceTech1(OpsProperties opsProperties) {
        super(
                SlackTeam.TECH1,
                opsProperties.getTech1SlackConfigs()
        );
    }
}
