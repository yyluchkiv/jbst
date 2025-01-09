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
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final Map<TeamV2, SlackBot> bots = new ConcurrentHashMap<>();

    public final void initialize() {
        var slacksConfigs = this.opsProperties.getSlacksConfigs().getValues();
        slacksConfigs.forEach(sc -> {
            var bot = new SlackBot(this.slackMessagingService, new SlackClient(sc));
            bot.initialize();
            this.bots.put(sc.getTeam(), bot);
        });
    }
}
