package jbst.ops.server.slack;

import jbst.ops.server.domain.servers.TeamV2;
import jbst.ops.server.properties.OpsProperties;
import jbst.ops.server.slack.slacks.SlackBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SlackBotsService {

    // Services
    private final SlackMessagingService slackMessagingService;
    // Properties
    private final OpsProperties opsProperties;

    // Services
    private final Map<TeamV2, SlackBot> bots = new ConcurrentHashMap<>();

    public final void initialize() {
        var sc1 = this.opsProperties.getTech1SlackConfigs();
        var sc2 = this.opsProperties.getSmartAppsSlackConfigs();
        this.bots.put(sc1.getTeam(), new SlackBot(sc1, this.slackMessagingService, new SlackClient(sc1)));
        this.bots.put(sc2.getTeam(), new SlackBot(sc2, this.slackMessagingService, new SlackClient(sc2)));
        this.bots.values().forEach(SlackBot::initialize);
    }
}
