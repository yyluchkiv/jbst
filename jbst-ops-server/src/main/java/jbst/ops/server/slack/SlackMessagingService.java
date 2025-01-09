package jbst.ops.server.slack;

import jbst.ops.server.domain.servers.TeamV2;
import jbst.ops.server.domain.slack.teams.SlackTeamEventV1;
import jbst.ops.server.properties.OpsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static jbst.foundation.domain.time.SchedulerConfiguration.EVERY_250_MILLISECONDS;

@Slf4j
@Service
public class SlackMessagingService {
    protected final BlockingQueue<List<SlackTeamEventV1>> sendingQueue = new LinkedBlockingQueue<>();

    // Services
    private final Map<TeamV2, SlackClient> slacksServices = new ConcurrentHashMap<>();

    @Autowired
    public SlackMessagingService(OpsProperties opsProperties) {
//        var slackConfigs1 = opsProperties.getTech1SlackConfigs();
//        var slackConfigs2 = opsProperties.getSmartAppsSlackConfigs();
//        this.slacksServices.put(slackConfigs1.getTeam(), new SlackClient(slackConfigs1));
//        this.slacksServices.put(slackConfigs2.getTeam(), new SlackClient(slackConfigs2));
//        this.configure();
    }

    public final void sendAsync(SlackTeamEventV1 slackTeamEventV1) {
        this.sendAsync(List.of(slackTeamEventV1));
    }

    public final void sendAsync(List<SlackTeamEventV1> slackTeamEventV1s) {
        try {
            this.sendingQueue.put(slackTeamEventV1s);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    private void configure() {
        newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
            try {
                var slackTeamEvents = this.sendingQueue.take();
                for (var slackTeamEvent : slackTeamEvents) {
                    var slackService = this.slacksServices.get(slackTeamEvent.getRequestContext().getSlackTeam());

                    var messageType = slackTeamEvent.getMessageType();

                    if (messageType.isCommunicationMain()) {
                        slackService.sendCommunicationMain(slackTeamEvent);
                    }

                    if (messageType.isCommunicationTeam()) {
                        slackService.sendCommunicationTeam(slackTeamEvent);
                    }
                }
            } catch (InterruptedException ex1) {
                Thread.currentThread().interrupt();
            } catch (RuntimeException ex2) {
                // ignore
            }
        }, EVERY_250_MILLISECONDS.initialDelay(), EVERY_250_MILLISECONDS.delay(), EVERY_250_MILLISECONDS.unit());
    }
}
