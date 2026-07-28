package jbst.foundation.incidents.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.domain.http.requests.JbstUserRequestMetadata;
import jbst.foundation.domain.properties.configs.JbstPropertyApp;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;
import static java.util.Comparator.naturalOrder;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static jbst.foundation.domain.constants.JbstConstants.DateTimeFormatters.DTF11;
import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;
import static jbst.foundation.domain.random.JbstRandom.randomString;
import static jbst.foundation.domain.strings.JbstTraces.getTrace;
import static jbst.foundation.incidents.domain.JbstIncident.Constants.Keys.*;
import static jbst.foundation.incidents.domain.JbstIncident.Constants.Types.THROWABLE;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Getter
@EqualsAndHashCode
@ToString
public class JbstIncident {
    private static final Comparator<Map.Entry<String, Object>> PRINT_COMPARATOR = Comparator.<Map.Entry<String, Object>, Boolean>comparing(element -> !element.getKey().equals(TYPE))
            .thenComparing(Map.Entry::getKey);

    private static final String PLAIN_MESSAGE_SEPARATOR = range(0, 160).mapToObj(item -> "·").collect(joining());

    private static final Set<String> PLAIN_MESSAGE_SKIPPED = Set.of(
            SERVER,
            TYPE,
            COUNTRY_FLAG
    );

    private static final List<String> ORDERED_PREFERRED_KEYS = new LinkedList<>(
            List.of(
                    WEBCLIENT,
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

    private final Map<String, Object> attributes;

    public JbstIncident() {
        this.attributes = new TreeMap<>();
    }

    public JbstIncident(Map<String, Object> attributes) {
        this.attributes = new TreeMap<>();
        this.addAll(attributes);
    }

    public JbstIncident(String type) {
        this();
        this.setType(type);
    }

    public JbstIncident(JbstSecurityJwtIncident type) {
        this(type.getValue());
    }

    public JbstIncident(JbstSecurityJwtIncident type, Username username) {
        this(type);
        this.addUsername(username);
    }

    public JbstIncident(JbstSecurityJwtIncident type, UsernamePasswordCredentials credentials) {
        this(type);
        this.addUsername(credentials.username());
        this.addPassword(credentials.password());
    }

    public JbstIncident(JbstSecurityJwtIncident type, Username username, JbstUserRequestMetadata userRequestMetadata) {
        this(type);
        this.addUsername(username);
        this.addUserRequestMetadata(userRequestMetadata);
    }

    public JbstIncident(JbstSecurityJwtIncident type, UsernamePasswordCredentials credentials, JbstUserRequestMetadata userRequestMetadata) {
        this(type, credentials);
        this.addUserRequestMetadata(userRequestMetadata);
    }

    public JbstIncident(Throwable throwable) {
        this(THROWABLE);
        this.add(EXCEPTION, throwable.getClass());
        this.add(TRACE, getTrace(throwable));
        this.add(MESSAGE, throwable.getMessage());
    }

    public JbstIncident(Throwable throwable, Method method, List<Object> params) {
        this(throwable);

        if (nonNull(method)) {
            this.add(METHOD, method.toString());
        }

        if (!isEmpty(params)) {
            this.add(PARAMS, params.stream().map(Object::toString).collect(Collectors.joining(JbstConstants.Symbols.COLLECTORS_COMMA_SPACE)));
        }
    }

    public JbstIncident(Throwable throwable, Map<String, Object> attributes) {
        this(throwable);

        if (!isEmpty(attributes)) {
            attributes.forEach(this::add);
        }
    }

    public static JbstIncident random() {
        var incident = new JbstIncident(randomString());
        incident.add(randomString(), randomString());
        incident.add(randomString(), randomString());
        incident.add(randomString(), randomString());
        return incident;
    }

    public static JbstIncident copyOf(@NotNull JbstIncident incident) {
        var instance = new JbstIncident();
        instance.addAll(incident.getAttributes());
        return instance;
    }

    public void add(String key, Object value) {
        this.attributes.put(key, value);
    }

    public void addAll(Map<String, Object> attributes) {
        this.attributes.putAll(attributes);
    }

    public void print() {
        LOGGER.info(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
        this.attributes.entrySet().stream()
                .sorted(PRINT_COMPARATOR)
                .forEach(entry -> LOGGER.info("{} — {}", entry.getKey(), entry.getValue()));
        LOGGER.info(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
    }

    public void setType(String type) {
        this.add(TYPE, type);
    }

    public void addServer(@NotNull JbstPropertyApp app) {
        this.add(SERVER, app.getName());
    }

    public void addWebClient(@NotNull JbstPropertyApp app) {
        if (nonNull(app.getWebclientURL())) {
            this.add(WEBCLIENT, app.getWebclientURL());
        }
    }

    public void addUsername(Username username) {
        this.add(USERNAME, username);
    }

    public void addPassword(Password password) {
        this.add(PASSWORD, password);
    }

    public void addUserRequestMetadata(JbstUserRequestMetadata userRequestMetadata) {
        var tupleResponseException = userRequestMetadata.getException();
        if (!tupleResponseException.isOk()) {
            this.add(EXCEPTION, tupleResponseException.getMessage());
        }

        var whereTuple3 = userRequestMetadata.getWhereTuple3();
        this.add(IP_ADDRESS, whereTuple3.a());
        this.add(COUNTRY_FLAG, whereTuple3.b());
        this.add(WHERE, whereTuple3.c());

        var whatTuple2 = userRequestMetadata.getWhatTuple2();
        this.add(BROWSER, whatTuple2.a());
        this.add(WHAT, whatTuple2.b());
    }

    @JsonIgnore
    public String getType() {
        var attribute = this.attributes.get(TYPE);
        return nonNull(attribute) ? attribute.toString() : JbstConstants.Strings.UNKNOWN;
    }

    @JsonIgnore
    public Username getUsername() {
        var attribute = this.attributes.get(USERNAME);
        return nonNull(attribute) ? Username.of(attribute.toString()) : Username.unknown();
    }

    // =================================================================================================================
    // a.k.a. "OpsIncident"
    // =================================================================================================================
    @JsonIgnore
    public boolean isJwtBased() {
        return JbstSecurityJwtIncident.asList().contains(this.getType());
    }

    @JsonIgnore
    public List<String> getOrderedKeys() {
        var variablesKeys = this.attributes.keySet().stream()
                .filter(entryKey -> !PLAIN_MESSAGE_SKIPPED.contains(entryKey))
                .toList();
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
    public String asTelegramPlainMessage() {
        var header = "<b>%s</b> on %s — %s (UTC)".formatted(
                this.getType(),
                this.attributes.get(SERVER),
                LocalDateTime.now(UTC).format(DTF11)
        );
        return PLAIN_MESSAGE_SEPARATOR + NEWLINE +
                header + NEWLINE +
                PLAIN_MESSAGE_SEPARATOR + NEWLINE +
                this.getOrderedKeys().stream()
                        .map(variableKey -> {
                            if (TRACE.equals(variableKey)) {
                                var trace = this.attributes.get(variableKey).toString();
                                // WARNING: ~4096 is max telegram message
                                return "trace: <pre>" + trace.substring(0, Math.min(trace.length(), 2000)) + "</pre>";
                            } else {
                                return variableKey + ": " + this.attributes.get(variableKey);
                            }
                        })
                        .collect(joining(NEWLINE));
    }

    // =================================================================================================================
    // CLASSES
    // =================================================================================================================
    @UtilityClass
    public static class Constants {
        @SuppressWarnings("unused")
        @UtilityClass
        public static class Keys {
            public static final String TYPE = "incidentType";

            public static final String SERVER = "server";
            public static final String WEBCLIENT = "webClient";

            public static final String EMAIL = "email";
            public static final String USERNAME = "username";
            public static final String PASSWORD = "password";

            public static final String TIMES = "_times";
            public static final String TRIGGER = "_trigger";

            public static final String EXCEPTION = "exception";
            public static final String TRACE = "trace";
            public static final String MESSAGE = "message";
            public static final String METHOD = "method";
            public static final String PARAMS = "params";

            public static final String INVITATION_CODE = "invitationCode";
            public static final String INVITATION_OWNER = "invitationOwner";

            public static final String BROWSER = "browser";
            public static final String COUNTRY = "country";
            public static final String COUNTRY_CODE = "countryCode";
            public static final String COUNTRY_FLAG = "countryFlag";
            public static final String IP_ADDRESS = "ipAddress";
            public static final String WHAT = "what";
            public static final String WHEN = "when";
            public static final String WHERE = "where";
        }

        @UtilityClass
        public static class Types {
            public static final String THROWABLE = "Throwable";
        }
    }
}
