package jbst.foundation.incidents.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.domain.http.requests.UserRequestMetadata;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static jbst.foundation.incidents.domain.IncidentAttributes.Keys.*;
import static jbst.foundation.utilities.exceptions.TraceUtility.getTrace;
import static jbst.foundation.utilities.random.RandomUtility.randomString;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class Incident {
    private static final Comparator<Map.Entry<String, Object>> PRINT_COMPARATOR = Comparator.<Map.Entry<String, Object>, Boolean>comparing(element -> !element.getKey().equals(TYPE))
            .thenComparing(Map.Entry::getKey);

    private final Map<String, Object> attributes;

    public Incident() {
        this.attributes = new TreeMap<>();
    }

    public Incident(Map<String, Object> attributes) {
        this.attributes = new TreeMap<>();
        this.addAll(attributes);
    }

    public Incident(String type) {
        this();
        this.setType(type);
    }

    public Incident(JbstSecurityJwtIncident type) {
        this(type.getValue());
    }

    public Incident(JbstSecurityJwtIncident type, Username username) {
        this(type);
        this.addUsername(username);
    }

    public Incident(JbstSecurityJwtIncident type, UsernamePasswordCredentials credentials) {
        this(type);
        this.addUsername(credentials.username());
        this.addPassword(credentials.password());
    }

    public Incident(JbstSecurityJwtIncident type, Username username, UserRequestMetadata userRequestMetadata) {
        this(type);
        this.addUsername(username);
        this.addUserRequestMetadata(userRequestMetadata);
    }

    public Incident(JbstSecurityJwtIncident type, UsernamePasswordCredentials credentials, UserRequestMetadata userRequestMetadata) {
        this(type, credentials);
        this.addUserRequestMetadata(userRequestMetadata);
    }

    public Incident(Throwable throwable) {
        this(IncidentAttributes.IncidentsTypes.THROWABLE);

        this.add(EXCEPTION, throwable.getClass());
        this.add(TRACE, getTrace(throwable));
        this.add(MESSAGE, throwable.getMessage());
    }

    public Incident(Throwable throwable, Method method, List<Object> params) {
        this(throwable);

        if (nonNull(method)) {
            this.add(METHOD, method.toString());
        }

        if (!isEmpty(params)) {
            this.add(PARAMS, params.stream().map(Object::toString).collect(Collectors.joining(JbstConstants.Symbols.COLLECTORS_COMMA_SPACE)));
        }
    }

    public Incident(Throwable throwable, Map<String, Object> attributes) {
        this(throwable);

        if (!isEmpty(attributes)) {
            attributes.forEach(this::add);
        }
    }

    public static Incident random() {
        var incident = new Incident(randomString());
        incident.add(randomString(), randomString());
        incident.add(randomString(), randomString());
        incident.add(randomString(), randomString());
        return incident;
    }

    public static Incident copyOf(@NotNull Incident incident) {
        var instance = new Incident();
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

    public void addUsername(Username username) {
        this.add(USERNAME, username);
    }

    public void addPassword(Password password) {
        this.add(PASSWORD, password);
    }

    public void addUserRequestMetadata(UserRequestMetadata userRequestMetadata) {
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
}
