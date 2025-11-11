package jbst.server.ops.domain.incidents;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jbst.foundation.domain.base.ServerName;
import jbst.foundation.domain.base.Username;
import jbst.foundation.incidents.domain.Incident;
import jbst.foundation.incidents.domain.IncidentAttributes;
import jbst.server.ops.domain.servers.Team;
import jbst.server.ops.properties.configs.JbstPropertyOpsRecipients;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;
import static java.util.Comparator.naturalOrder;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static jbst.foundation.domain.constants.JbstConstants.DateTimeFormatters.DTF11;
import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;
import static jbst.foundation.domain.constants.JbstConstants.Symbols.TWO_NEWLINE;
import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static jbst.foundation.incidents.domain.IncidentAttributes.Keys.*;
import static jbst.foundation.utilities.time.LocalDateUtility.now;

// Lombok
@SuppressWarnings("ClassCanBeRecord")
@Data
public class OpsIncident {
    private static final String PLAIN_MESSAGE_SEPARATOR = range(0, 124).mapToObj(item -> "·").collect(joining());

    public static final String TIMES = "_times";
    private static final String MEMBERS = "members";
    private static final String YEAR = "year";
    private static final String SERVER_URL = "serverURL";
    private static final String REMOTE_URL = "remoteURL";
    private static final String WHEN_UA = "whenUA";
    private static final String WHEN_UTC = "whenUTC";

    private static final Set<String> PLAIN_MESSAGE_SKIPPED = Set.of(
            MEMBERS,
            YEAR,
            SERVER_URL,
            REMOTE_URL,
            WHEN_UA,
            WHEN_UTC
    );

    protected static final List<String> ORDERED_PREFERRED_KEYS = new LinkedList<>(
            List.of(
                    TIMES,
                    TRIGGER,
                    USERNAME,
                    PASSWORD,
                    INVITATION_CODE,
                    BROWSER,
                    COUNTRY_FLAG,
                    IP_ADDRESS,
                    WHERE,
                    WHAT,
                    WHEN,
                    EXCEPTION,
                    MESSAGE,
                    METHOD,
                    PARAMS,
                    TRACE
            )
    );

    private final Team team;
    private final Set<String> to;
    private final String incidentType;
    private final Username username;
    private final String emailSubject;
    private final Map<String, Object> variables;

    public static OpsIncident of(
            Incident incident,
            ServerName serverName,
            Team team,
            String serverURL,
            OpsIncidentEnv env,
            JbstPropertyOpsRecipients recipientsConfigs
    ) {
        var incidentType = incident.getType();
        var username = incident.getUsername();
        var readableServerName = "\"" + serverName.value() + "\"";
        var emailSubject = "[OpsIncidents] " + incidentType +
                " on " + readableServerName +
                " — " + LocalDateTime.now(UTC).format(DTF11) + " (UTC)";

        Set<String> to = new HashSet<>(recipientsConfigs.getTo());
        Map<String, Object> variables = new HashMap<>();
        variables.put(MEMBERS, to.stream().map(email -> "@" + email.split("@")[0]).collect(joining(", ")));
        variables.put(YEAR, now(UTC).getYear());
        variables.put(SERVER_URL, serverURL);
        variables.put(REMOTE_URL, env.getRemoteHost());
        variables.put(WHEN_UA, LocalDateTime.now(UKRAINE).format(DTF11) + " in Ukraine");
        variables.put(WHEN_UTC, LocalDateTime.now(UTC).format(DTF11) + " in UK");

        incident.getAttributes().entrySet().stream()
                .filter(entry -> !IncidentAttributes.Keys.TYPE.equals(entry.getKey()))
                .forEach(entry -> {
                    if (TRACE.equals(entry.getKey())) {
                        variables.put(
                                entry.getKey(),
                                "Problem occurred! Please take required actions!\n\n" + entry.getValue().toString()
                        );
                    } else {
                        variables.put(entry.getKey(), entry.getValue());
                    }
                });
        return new OpsIncident(
                team,
                to,
                incidentType,
                username,
                emailSubject,
                variables
        );
    }

    @JsonIgnore
    public String getSlackHeader() {
        var message = ":ladybug: Please review incident details and *take actions* to stabilize production environment :ladybug:";
        return "<!here>" + TWO_NEWLINE + message + NEWLINE;
    }

    @JsonIgnore
    public List<String> getOrderedVariablesKeys() {
        var variablesKeys = this.variables.keySet().stream()
                .filter(entryKey -> !PLAIN_MESSAGE_SKIPPED.contains(entryKey))
                .collect(Collectors.toList());
        List<String> orderedKeys = new LinkedList<>();
        ORDERED_PREFERRED_KEYS.forEach(preferredKey -> {
            if (variablesKeys.contains(preferredKey)) {
                orderedKeys.add(preferredKey);
                variablesKeys.remove(preferredKey);
            }
        });
        variablesKeys.sort(naturalOrder());
        orderedKeys.addAll(variablesKeys);
        return orderedKeys;
    }

    @JsonIgnore
    public String getTrace() {
        return this.variables.get(TRACE).toString();
    }

    @JsonIgnore
    public String getPlainMessage() {
        return PLAIN_MESSAGE_SEPARATOR + NEWLINE +
                SERVER_URL + ": " + this.variables.get(SERVER_URL) + NEWLINE +
                REMOTE_URL + ": " + this.variables.get(REMOTE_URL) + NEWLINE +
                PLAIN_MESSAGE_SEPARATOR + NEWLINE +
                this.getOrderedVariablesKeys().stream()
                        .map(variableKey -> {
                            if (TRACE.equals(variableKey)) {
                                var trace = this.variables.get(variableKey).toString();
                                // WARNING: ~3000 is max to use as Slack formatted message
                                return variableKey + ": " + trace.substring(0, Math.min(trace.length(), 2500));
                            } else {
                                return variableKey + ": " + this.variables.get(variableKey);
                            }
                        })
                        .collect(joining(NEWLINE));
    }
}
