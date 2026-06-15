package jbst.foundation.incidents.feigns.clients;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.feigns.telegram.JbstTelegram;
import jbst.foundation.incidents.domain.JbstIncident;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static jbst.foundation.domain.constants.JbstConstants.Logs.SERVER_OFFLINE;
import static jbst.foundation.domain.time.JbstTime.getCurrentTimestamp;
import static jbst.foundation.incidents.domain.JbstIncident.Constants.Keys.TIMES;
import static jbst.foundation.incidents.domain.JbstIncident.Constants.Keys.TRACE;
import static jbst.foundation.incidents.domain.JbstIncident.Constants.Types.THROWABLE;

@Slf4j
public class JbstIncidentClientTypeTelegram implements JbstIncidentClient {

    // TODO [YYL-incidents] introduce incidents-settings -> jbst-settings
    private static final List<JbstIncidentTraceConfiguration> TRACES = List.of(
            new JbstIncidentTraceConfiguration(
                    false,
                    "org.springframework.beans.factory.BeanCreationNotAllowedException",
                    "Spring Events Redeployment Failure"
            ),
            new JbstIncidentTraceConfiguration(
                    false,
                    "Singleton bean creation not allowed while singletons of this factory are in destruction",
                    "Spring Events Redeployment Failure"
            ),
            new JbstIncidentTraceConfiguration(
                    false,
                    "com.neovisionaries.ws.client.InsufficientDataException: The end of the stream has been reached unexpectedly",
                    "Websocket Reconnect Issue"
            ),
            new JbstIncidentTraceConfiguration(
                    false,
                    "com.neovisionaries.ws.client.WebSocketException: The RSV1 bit of a frame is set unexpectedly",
                    "Websocket Reconnect Issue"
            )
    );

    // Data
    private final ScheduledExecutorService scheduledExecutorService = newSingleThreadScheduledExecutor();
    private final ConcurrentHashMap<JbstIncident, JbstIncidentConcurrentStats> incidents = new ConcurrentHashMap<>();

    // Service
    private final JbstTelegram telegram;
    // Properties
    private final JbstProperties jbstProperties;

    public JbstIncidentClientTypeTelegram(JbstTelegram telegram, JbstProperties jbstProperties) {
        this.telegram = telegram;
        this.jbstProperties = jbstProperties;
        this.configureCleanCronJob();
    }

    @Override
    public void registerIncident(@NotNull JbstIncident incident) {
        incident.addServer(this.jbstProperties.getApp().getName());
        this.registerIncidentPlainBased(incident);
    }

    // WARNING #1: every 15 seconds check on incident "times" == 10 -> register incident + NO cleanup
    // WARNING #2: every 15 seconds check on incident "lastTime" was more than 15 minutes ago -> register incident + cleanup
    public final void configureCleanCronJob() {
        this.scheduledExecutorService.scheduleWithFixedDelay(() -> {
            var incidentsItt = this.incidents.entrySet().iterator();
            while (incidentsItt.hasNext()) {
                var entry = incidentsItt.next();
                var incidentStats = entry.getValue();

                if (incidentStats.getExecutedTimesDifferenceFlagAndUpdatePreviousIfIncidentRegistrationRequired()) {
                    var incident = JbstIncident.copyOf(entry.getKey());
                    incident.add(TIMES, incidentStats.getTimes());
                    this.registerIncidentPlainBased(incident);
                }

                if (MILLISECONDS.toMinutes(getCurrentTimestamp() - incidentStats.getLastTime()) >= 15) {
                    if (incidentStats.isExecutedMoreThanOnce()) {
                        var incident = JbstIncident.copyOf(entry.getKey());
                        incident.add(TIMES, incidentStats.getTimes());
                        this.registerIncidentPlainBased(incident);
                    }
                    incidentsItt.remove();
                }
            }
        }, 0, 15, TimeUnit.SECONDS);
    }

    // =================================================================================================================
    // PRIVATE CLASSES
    // =================================================================================================================
    private record JbstIncidentTraceConfiguration(boolean enabled, String trace, String incidentType) {}

    private static class JbstIncidentConcurrentStats {
        private final AtomicInteger currentTimes;
        private final AtomicInteger previousTimes;
        private final AtomicLong lastTime;

        @SuppressWarnings("unused")
        public JbstIncidentConcurrentStats(JbstIncident incident) {
            this.currentTimes = new AtomicInteger(1);
            this.previousTimes = new AtomicInteger(1);
            this.lastTime = new AtomicLong(getCurrentTimestamp());
        }

        public void incrementStats() {
            this.currentTimes.incrementAndGet();
            this.lastTime.set(getCurrentTimestamp());
        }

        public long getLastTime() {
            return this.lastTime.get();
        }

        public long getTimes() {
            return this.currentTimes.get();
        }

        public boolean isExecutedMoreThanOnce() {
            return this.currentTimes.get() > 1;
        }

        public boolean getExecutedTimesDifferenceFlagAndUpdatePreviousIfIncidentRegistrationRequired() {
            var difference = this.currentTimes.get() - this.previousTimes.get();
            var registerIncident = difference > 10;
            if (registerIncident) {
                this.previousTimes.addAndGet(difference);
            }
            return registerIncident;
        }
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private void registerIncidentPlainBased(JbstIncident incident) {
        try {
            if (incident.isJwtBased()) {
                this.telegram.sendIncident(incident);
            } else {
                var skip = this.filterOnConfigsAndReturnSkip(incident);
                if (!skip && this.isNew(incident)) {
                    this.telegram.sendIncident(incident);
                }
            }
        } catch (Exception ex) {
            LOGGER.error(SERVER_OFFLINE, "telegram", ex.getMessage());
            incident.print();
        }
    }

    private boolean isNew(JbstIncident incident) {
        if (this.incidents.containsKey(incident)) {
            this.incidents.get(incident).incrementStats();
            return false;
        } else {
            this.incidents.put(incident, new JbstIncidentConcurrentStats(incident));
            return true;
        }
    }

    private boolean filterOnConfigsAndReturnSkip(JbstIncident incident) {
        if (!THROWABLE.equals(incident.getType())) {
            return false;
        }
        var trace = incident.getAttributes().get(TRACE).toString();
        var traceConfigsOpt = TRACES.stream()
                .filter(item -> trace.contains(item.trace))
                .findFirst();
        if (traceConfigsOpt.isPresent()) {
            var traceConfigs = traceConfigsOpt.get();
            incident.setType(traceConfigs.incidentType);
            return !traceConfigs.enabled();
        } else {
            return false;
        }
    }
}
