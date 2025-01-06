package jbst.ops.server.slack.messaging.impl;

import jbst.foundation.domain.time.SchedulerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jbst.ops.server.domain.slack.teams.SlackTeam;
import jbst.ops.server.domain.slack.teams.SlackTeamEvent;
import jbst.ops.server.slack.messaging.SlackMessagingService;
import jbst.ops.server.slack.services.AbstractSlackService;
import jbst.ops.server.slack.services.SlackServiceSmartApps;
import jbst.ops.server.slack.services.SlackServiceTech1;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Slf4j
@Service
public class SlackMessagingServiceImpl implements SlackMessagingService {
    private static final SchedulerConfiguration MESSAGING_INTERVAL = new SchedulerConfiguration(250L, 250L, MILLISECONDS);

    protected final BlockingQueue<List<SlackTeamEvent>> sendingQueue = new LinkedBlockingQueue<>();

    // Services
    private final Map<SlackTeam, AbstractSlackService> slacksServices = new ConcurrentHashMap<>();

    @Autowired
    public SlackMessagingServiceImpl(SlackServiceSmartApps slackServiceSmartApps, SlackServiceTech1 slackServiceTech1) {
        this.slacksServices.put(slackServiceSmartApps.getSlackTeam(), slackServiceSmartApps);
        this.slacksServices.put(slackServiceTech1.getSlackTeam(), slackServiceTech1);
        this.configure();
    }

    @Override
    public void configure() {
        newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
            try {
                var slackTeamEvents = this.sendingQueue.take();
                for (var slackTeamEvent : slackTeamEvents) {
                    var slackService = this.slacksServices.get(slackTeamEvent.getRequestContext().getSlackTeam());

                    var messageType = slackTeamEvent.getMessageType();

                    if (messageType.isDirectOrChannel()) {
                        slackService.sendDirectOrChannel(slackTeamEvent);
                    }

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
        }, MESSAGING_INTERVAL.initialDelay(), MESSAGING_INTERVAL.delay(), MESSAGING_INTERVAL.unit());
    }

    @Override
    public void send(SlackTeamEvent slackTeamEvent) {
        this.send(List.of(slackTeamEvent));
    }

    @Override
    public void send(List<SlackTeamEvent> slackTeamEvents) {
        try {
            this.sendingQueue.put(slackTeamEvents);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
