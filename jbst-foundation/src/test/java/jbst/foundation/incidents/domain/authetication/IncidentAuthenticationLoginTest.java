package jbst.foundation.incidents.domain.authetication;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.geo.JbstGeoLocation;
import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.domain.http.requests.UserAgentDetails;
import jbst.foundation.domain.http.requests.UserRequestMetadata;
import org.junit.jupiter.api.Test;

import static jbst.foundation.domain.constants.JbstConstants.Flags.UNKNOWN;
import static org.assertj.core.api.Assertions.assertThat;

class IncidentAuthenticationLoginTest {

    @Test
    void convertAuthenticationLoginIncidentExceptionTest() {
        // Arrange
        var username = Username.hardcoded();
        var incident = new IncidentAuthenticationLogin(
                username,
                UserRequestMetadata.processed(
                        JbstGeoLocation.unknown(new IPAddress("8.8.8.8"), "exception1"),
                        UserAgentDetails.processing()
                )
        );

        // Act
        var actual = incident.getPlainIncident();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getType()).isEqualTo("Authentication Login");
        assertThat(actual.getUsername().value()).isEqualTo("jbst");
        assertThat(actual.getAttributes())
                .hasSize(8)
                .containsOnlyKeys("incidentType", "username", "browser", "countryFlag", "ipAddress", "what", "where", "exception")
                .containsEntry("incidentType", "Authentication Login")
                .containsEntry("username", username)
                .containsEntry("browser", "[?]")
                .containsEntry("countryFlag", "🏴‍")
                .containsEntry("ipAddress", "8.8.8.8")
                .containsEntry("what", "[?], [?] on [?]")
                .containsEntry("where", "🏴‍ Unknown, Unknown")
                .containsEntry("exception", "exception1");
    }

    @Test
    void convertAuthenticationLoginIncidentTest() {
        // Arrange
        var username = Username.hardcoded();
        var incident = new IncidentAuthenticationLogin(
                username,
                UserRequestMetadata.processing(new IPAddress("127.0.0.1"))
        );

        // Act
        var actual = incident.getPlainIncident();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getType()).isEqualTo("Authentication Login");
        assertThat(actual.getUsername().value()).isEqualTo("jbst");
        assertThat(actual.getAttributes())
                .hasSize(7)
                .containsOnlyKeys("incidentType", "username", "browser", "countryFlag", "ipAddress", "what", "where")
                .containsEntry("incidentType", "Authentication Login")
                .containsEntry("username", username)
                .containsEntry("browser", "[?]")
                .containsEntry("countryFlag", UNKNOWN)
                .containsEntry("ipAddress", "127.0.0.1")
                .containsEntry("what", "—")
                .containsEntry("where", "Processing. Please wait...");
    }
}
