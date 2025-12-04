package jbst.foundation.incidents.domain.authetication;

import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.http.requests.JbstUserRequestMetadata;
import org.junit.jupiter.api.Test;

import static jbst.foundation.domain.constants.JbstConstants.Flags.UKRAINE;
import static org.assertj.core.api.Assertions.assertThat;

class IncidentAuthenticationLoginFailureUsernamePasswordTest {

    @Test
    void convertAuthenticationLoginFailureUsernamePasswordIncidentTest() {
        // Arrange
        var incident = new IncidentAuthenticationLoginFailureUsernamePassword(
                UsernamePasswordCredentials.hardcoded(),
                JbstUserRequestMetadata.valid()
        );

        // Act
        var actual = incident.getPlainIncident();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getType()).isEqualTo("Authentication Login Failure Username/Password");
        assertThat(actual.getUsername().value()).isEqualTo("jbst");
        assertThat(actual.getAttributes())
                .hasSize(8)
                .containsOnlyKeys("incidentType", "username", "password", "browser", "countryFlag", "ipAddress", "what", "where")
                .containsEntry("incidentType", "Authentication Login Failure Username/Password")
                .containsEntry("username", incident.credentials().username())
                .containsEntry("password", incident.credentials().password())
                .containsEntry("browser", "Chrome")
                .containsEntry("countryFlag", UKRAINE)
                .containsEntry("ipAddress", "127.0.0.1")
                .containsEntry("what", "Chrome, macOS on Desktop")
                .containsEntry("where", "🇺🇦 Ukraine, Lviv");
    }
}
