package jbst.ops.server.slack;

import com.slack.api.Slack;
import jbst.ops.server.domain.servers.Team;
import jbst.ops.server.domain.slack.bots.SlackBot;
import jbst.ops.server.properties.OpsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SlackBotsService {

    // Services
    private final SlackCommandsService slackCommandsService;
    // Properties
    private final OpsProperties opsProperties;

    // Services
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final Map<Team, SlackBot> bots = new ConcurrentHashMap<>();

    public final void initialize() {
        var slacksConfigs = this.opsProperties.getSlacksConfigs().getValues();
        slacksConfigs.forEach(sc -> {
            var bot = new SlackBot(sc, this.slackCommandsService, Slack.getInstance().methods(sc.getBotToken()));
            bot.initialize();
            this.bots.put(sc.getTeam(), bot);
        });
    }

    public final void sendMainBotMainCommunication(List<String> messages) {
        this.getMainSlackBot().sendMainCommunication(messages);
    }

    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private SlackBot getMainSlackBot() {
        return this.bots.values().stream()
                .filter(bot -> bot.configs().isMain())
                .findFirst()
                .get();
    }
}
