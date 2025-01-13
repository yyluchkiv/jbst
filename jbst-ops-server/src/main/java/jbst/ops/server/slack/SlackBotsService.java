package jbst.ops.server.slack;

import com.slack.api.Slack;
import jbst.ops.server.domain.incidents.OpsIncident;
import jbst.ops.server.domain.servers.Team;
import jbst.ops.server.domain.slack.bots.SlackBot;
import jbst.ops.server.properties.OpsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.nonNull;

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

    public final void sendMainTeamIncident(OpsIncident opsIncident) {
        var slackBotOpt = this.getMainSlackBot();
        slackBotOpt.ifPresent(slackBot -> {
            slackBot.sendMainCommunication(opsIncident.getSlackHeader());
            slackBot.sendMainCommunication(opsIncident.getPlainMessage());
        });
    }

    public final void sendIncident(OpsIncident opsIncident) {
        var team = opsIncident.getTeam();
        var slackBot = this.bots.get(team);
        if (nonNull(slackBot)) {
            slackBot.sendTeamCommunication(opsIncident.getSlackHeader(), team);
            slackBot.sendTeamCommunication(opsIncident.getPlainMessage(), team);
        }
    }

    public final void sendMainBotMainCommunication(List<String> messages) {
        var slackBotOpt = this.getMainSlackBot();
        slackBotOpt.ifPresent(slackBot -> slackBot.sendMainCommunication(messages));
    }

    public final void sendMainCommunication(List<String> messages, Team team) {
        var bot = this.bots.get(team);
        if (nonNull(bot)) {
            bot.sendMainCommunication(messages);
        }
    }

    public final void sendMainCommunication(Map<Team, List<String>> teamsMessages) {
        teamsMessages.forEach((team, messages) -> {
            var bot = this.bots.get(team);
            if (nonNull(bot)) {
                bot.sendMainCommunication(messages);
            }
        });
    }

    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    private Optional<SlackBot> getMainSlackBot() {
        return this.bots.values().stream()
                .filter(bot -> bot.configs().isMain())
                .findFirst();
    }
}
