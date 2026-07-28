package jbst.foundation.incidents.domain.authetication;

import jbst.foundation.domain.base.Username;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JbstIncidentAuthenticationLogoutMinTest {

    @Test
    void convertAuthenticationLogoutMinIncidentTest() {
        // Arrange
        var username = Username.fixed();
        var incident = new JbstIncidentAuthenticationLogoutMin(
                username
        );

        // Act
        var actual = incident.getPlainIncident();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getType()).isEqualTo("Authentication Logout Min");
        assertThat(actual.getUsername().value()).isEqualTo("jbst");
        assertThat(actual.getAttributes()).hasSize(2);
        assertThat(actual.getAttributes()).containsOnlyKeys("incidentType", "username");
        assertThat(actual.getAttributes()).containsEntry("incidentType", "Authentication Logout Min");
        assertThat(actual.getAttributes()).containsEntry("username", username);
    }
}
