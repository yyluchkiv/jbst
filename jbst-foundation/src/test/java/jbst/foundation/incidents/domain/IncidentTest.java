package jbst.foundation.incidents.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static jbst.foundation.domain.random.JbstRandom.randomMethod;
import static jbst.foundation.domain.random.JbstRandom.randomString;
import static org.assertj.core.api.Assertions.assertThat;

class IncidentTest {

    private static Stream<Arguments> getTypeTest() {
        return Stream.of(
                Arguments.of(Map.of(randomString(), randomString()), "Unknown"),
                Arguments.of(Map.of("incidentType", "IncidentType123"), "IncidentType123")
        );
    }

    private static Stream<Arguments> getUsernameTest() {
        return Stream.of(
                Arguments.of(Map.of(randomString(), randomString()), "Unknown"),
                Arguments.of(Map.of("username", "Username123"), "Username123")
        );
    }

    @Test
    void copyOfTest() {
        // Arrange
        var incident = Incident.random();

        // Act
        var copy = Incident.copyOf(incident);

        // Assert
        assertThat(copy.getAttributes()).isEqualTo(incident.getAttributes());
        assertThat(copy.getType()).isEqualTo(incident.getType());
        assertThat(copy.getUsername()).isEqualTo(incident.getUsername());
    }


    @ParameterizedTest
    @MethodSource("getTypeTest")
    void getTypeTest(Map<String, Object> attributes, String expected) {
        // Arrange
        var incident = new Incident();
        incident.addAll(attributes);

        // Act
        var actual = incident.getType();

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getUsernameTest")
    void getUsernameTest(Map<String, Object> attributes, String expected) {
        // Arrange
        var incident = new Incident();
        incident.addAll(attributes);

        // Act
        var actual = incident.getUsername();

        // Assert
        assertThat(actual.value()).isEqualTo(expected);
    }

    @Test
    void convertThrowableIncident1Test() {
        // Arrange
        var throwable = new NullPointerException("jbst");

        // Act
        var actual = new Incident(throwable);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getType()).isEqualTo("Throwable");
        assertThat(actual.getUsername().value()).isEqualTo("Unknown");
        assertThat(actual.getAttributes()).hasSize(4);
        assertThat(actual.getAttributes()).containsOnlyKeys("incidentType", "exception", "message", "trace");
        assertThat(actual.getAttributes()).containsEntry("incidentType", "Throwable");
        assertThat(actual.getAttributes()).containsEntry("exception", NullPointerException.class);
        assertThat(actual.getAttributes()).containsEntry("message", "jbst");
        assertThat(actual.getAttributes().get("trace").toString()).startsWith("java.lang.NullPointerException: jbst");
        assertThat(actual.getAttributes().get("trace").toString()).contains("at jbst.foundation.incidents.domain.IncidentTest.convertThrowableIncident1Test");
    }

    @Test
    void convertThrowableIncident2Test() {
        // Arrange
        var object = new Object();
        var throwable = new NullPointerException("jbst");
        var method = randomMethod();
        var params = List.of(object, "param1", 1L);

        // Act
        var actual = new Incident(throwable, method, params);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getType()).isEqualTo("Throwable");
        assertThat(actual.getUsername().value()).isEqualTo("Unknown");
        assertThat(actual.getAttributes()).hasSize(6);
        assertThat(actual.getAttributes()).containsOnlyKeys("incidentType", "exception", "message", "trace", "method", "params");
        assertThat(actual.getAttributes()).containsEntry("incidentType", "Throwable");
        assertThat(actual.getAttributes()).containsEntry("exception", NullPointerException.class);
        assertThat(actual.getAttributes()).containsEntry("message", "jbst");
        assertThat(actual.getAttributes()).containsEntry("params", object + ", param1, 1");
        assertThat(actual.getAttributes().get("trace").toString()).startsWith("java.lang.NullPointerException: jbst");
        assertThat(actual.getAttributes().get("trace").toString()).contains("at jbst.foundation.incidents.domain.IncidentTest.convertThrowableIncident2Test");
        assertThat(actual.getAttributes().get("method").toString()).contains("protected void java.lang.Object.finalize() throws java.lang.Throwable");
    }

    @Test
    void convertThrowableIncident3Test() {
        // Arrange
        var object = new Object();
        var throwable = new NullPointerException("jbst");
        var attributes = Map.of("key1", object);

        // Act
        var actual = new Incident(throwable, attributes);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getType()).isEqualTo("Throwable");
        assertThat(actual.getUsername().value()).isEqualTo("Unknown");
        assertThat(actual.getAttributes()).hasSize(5);
        assertThat(actual.getAttributes()).containsOnlyKeys("incidentType", "exception", "message", "trace", "key1");
        assertThat(actual.getAttributes()).containsEntry("incidentType", "Throwable");
        assertThat(actual.getAttributes()).containsEntry("exception", NullPointerException.class);
        assertThat(actual.getAttributes()).containsEntry("message", "jbst");
        assertThat(actual.getAttributes()).containsEntry("key1", object);
        assertThat(actual.getAttributes().get("trace").toString()).startsWith("java.lang.NullPointerException: jbst");
        assertThat(actual.getAttributes().get("trace").toString()).contains("at jbst.foundation.incidents.domain.IncidentTest.convertThrowableIncident3Test");
    }
}
