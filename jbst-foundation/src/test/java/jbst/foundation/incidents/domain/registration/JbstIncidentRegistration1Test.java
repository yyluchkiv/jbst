package jbst.foundation.incidents.domain.registration;

import jbst.foundation.domain.base.Username;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JbstIncidentRegistration1Test {

    @Test
    void convertRegister1IncidentTest() {
        // Arrange
        var username = Username.hardcoded();
        var incident = new JbstIncidentRegistration1(
                username
        );

        // Act
        var actual = incident.getPlainIncident();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getType()).isEqualTo("Register1");
        assertThat(actual.getUsername().value()).isEqualTo("jbst");
        assertThat(actual.getAttributes())
                .hasSize(2)
                .containsOnlyKeys("incidentType", "username")
                .containsEntry("incidentType", "Register1")
                .containsEntry("username", username);
    }
}
