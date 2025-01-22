package jbst.ops.server.services;

import jbst.foundation.domain.properties.base.JbstIamIncidentType;
import jbst.foundation.domain.tuples.Tuple2;
import jbst.foundation.domain.tuples.Tuple3;
import jbst.foundation.domain.tuples.TuplePresence;
import jbst.foundation.incidents.domain.Incident;
import jbst.foundation.services.emails.domain.EmailHTML;
import jbst.foundation.services.emails.domain.EmailPlainAttachment;
import jbst.foundation.services.emails.services.EmailService;
import jbst.ops.server.domain.incidents.OpsConcurrentIncidentStats;
import jbst.ops.server.domain.incidents.OpsIncidentEnv;
import jbst.ops.server.domain.incidents.OpsIncidentHTML;
import jbst.ops.server.properties.OpsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static jbst.foundation.incidents.domain.IncidentAttributes.IncidentsTypes.THROWABLE;
import static jbst.foundation.incidents.domain.IncidentAttributes.Keys.TRACE;
import static jbst.foundation.utilities.time.TimestampUtility.getCurrentTimestamp;
import static jbst.ops.server.domain.incidents.OpsIncident.TIMES;
import static jbst.ops.server.domain.incidents.OpsIncidentHTML.opsAnyIncident;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IncidentsService {

    // Services
    private final NotificationsService notificationsService;
    private final MonitoringService monitoringService;
    private final EmailService emailService;
    // Properties
    private final OpsProperties opsProperties;

    // IncidentType <-> HTML template name
    private static final Map<String, OpsIncidentHTML> TEMPLATES_MAPPINGS = Arrays.stream(JbstIamIncidentType.values())
            .map(type -> new Tuple2<>(type.toString(), opsAnyIncident()))
            .collect(Collectors.toMap(Tuple2::a, Tuple2::b));

    // enabled <-> trace <-> incidentType
    private static final List<Tuple3<Boolean, String, String>> TRACES = List.of(
            new Tuple3<>(
                    false,
                    "org.springframework.beans.factory.BeanCreationNotAllowedException",
                    "Spring Events Redeployment Failure"
            ),
            new Tuple3<>(
                    false,
                    "Singleton bean creation not allowed while singletons of this factory are in destruction",
                    "Spring Events Redeployment Failure"
            ),
            new Tuple3<>(
                    true,
                    "com.neovisionaries.ws.client.InsufficientDataException: The end of the stream has been reached unexpectedly",
                    "Websocket Reconnect Issue"
            ),
            new Tuple3<>(
                    true,
                    "com.neovisionaries.ws.client.WebSocketException: The RSV1 bit of a frame is set unexpectedly",
                    "Websocket Reconnect Issue"
            )
    );

    private final ScheduledExecutorService scheduledExecutorService = newSingleThreadScheduledExecutor();
    private final ConcurrentHashMap<Tuple2<Incident, OpsIncidentEnv>, OpsConcurrentIncidentStats> incidents = new ConcurrentHashMap<>();

    @EventListener
    public void onEvent(Incident incident) {
        this.registerIncident(incident, this.opsProperties.getOpsIncidentEnv());
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
                    var incident = Incident.copyOf(entry.getKey().a());
                    incident.add(TIMES, incidentStats.getTimes());
                    this.registerIncidentPlainBased(incident, entry.getValue().getEnv());
                }

                if (MILLISECONDS.toMinutes(getCurrentTimestamp() - incidentStats.getLastTime()) >= 15) {
                    if (incidentStats.isExecutedMoreThanOnce()) {
                        var incident = Incident.copyOf(entry.getKey().a());
                        incident.add(TIMES, incidentStats.getTimes());
                        this.registerIncidentPlainBased(incident, entry.getValue().getEnv());
                    }
                    incidentsItt.remove();
                }
            }
        }, 0, 15, TimeUnit.SECONDS);
    }

    public final void registerIncident(Incident incident, OpsIncidentEnv env) {
        var incidentHasPredefinedHTML = this.isIncidentHasPredefinedHTML(incident);
        if (incidentHasPredefinedHTML.present()) {
            this.registerIncidentHTMLBased(incident, env, incidentHasPredefinedHTML.value());
        } else {
            var skip = this.filterOnConfigsAndReturnSkip(incident);
            if (!skip && this.isNew(incident, env)) {
                this.registerIncidentPlainBased(incident, env);
            }
        }
    }

    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    private TuplePresence<OpsIncidentHTML> isIncidentHasPredefinedHTML(Incident incident) {
        var incidentType = incident.getType();
        return new TuplePresence<>(
                TEMPLATES_MAPPINGS.containsKey(incidentType),
                TEMPLATES_MAPPINGS.get(incidentType)
        );
    }

    private void registerIncidentPlainBased(Incident incident, OpsIncidentEnv env) {
        var opsIncident = this.monitoringService.getOpsIncident(incident, env);
        if (THROWABLE.equals(incident.getType())) {
            this.notificationsService.notifyIncident(opsIncident);
            this.emailService.sendPlainAttachment(
                    new EmailPlainAttachment(
                            opsIncident.getTo(),
                            opsIncident.getEmailSubject(),
                            opsIncident.getPlainMessage(),
                            "incident-trace.txt",
                            opsIncident.getTrace()
                    )
            );
        } else {
            this.emailService.sendPlain(
                    opsIncident.getTo(),
                    opsIncident.getEmailSubject(),
                    opsIncident.getPlainMessage()
            );
        }
    }

    private void registerIncidentHTMLBased(Incident incident, OpsIncidentEnv env, OpsIncidentHTML htmlTemplate) {
        var opsIncident = this.monitoringService.getOpsIncident(incident, env);
        this.emailService.sendHTML(
                new EmailHTML(
                        opsIncident.getTo(),
                        opsIncident.getEmailSubject(),
                        htmlTemplate.name(),
                        opsIncident.getVariables()
                )
        );
    }

    private boolean isNew(Incident incident, OpsIncidentEnv env) {
        var incidentOnEnv = new Tuple2<>(incident, env);
        if (this.incidents.containsKey(incidentOnEnv)) {
            this.incidents.get(incidentOnEnv).incrementStats();
            return false;
        } else {
            this.incidents.put(incidentOnEnv, new OpsConcurrentIncidentStats(env));
            return true;
        }
    }

    private boolean filterOnConfigsAndReturnSkip(Incident incident) {
        if (!THROWABLE.equals(incident.getType())) {
            return false;
        }
        var trace = incident.getAttributes().get(TRACE).toString();
        var traceConfigsOpt = TRACES.stream()
                .filter(item -> trace.contains(item.b()))
                .findFirst();
        if (traceConfigsOpt.isPresent()) {
            var traceConfigs = traceConfigsOpt.get();
            incident.setType(traceConfigs.c());
            return !traceConfigs.a();
        } else {
            return false;
        }
    }
}
