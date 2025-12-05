package jbst.foundation.incidents.domain.session;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.geo.JbstGeoLocation;
import jbst.foundation.domain.base.IPAddress;
import jbst.foundation.domain.http.requests.JbstUserAgentDetails;
import jbst.foundation.domain.http.requests.JbstUserRequestMetadata;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JbstIncidentSessionExpiredTest {

    @Test
    void convertSessionExpiredIncidentTest() {
        // Arrange
        var username = Username.hardcoded();
        var incident = new JbstIncidentSessionExpired(
                username,
                JbstUserRequestMetadata.processed(
                        JbstGeoLocation.processed(new IPAddress("2.2.2.2"), "UK", "UK", JbstConstants.Flags.UK, "London"),
                        JbstUserAgentDetails.processed("Mozilla", "MacOS", "Desktop")
                )
        );

        // Act
        var actual = incident.getPlainIncident();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getType()).isEqualTo("Session Expired");
        assertThat(actual.getUsername().value()).isEqualTo("jbst");
        assertThat(actual.getAttributes())
                .hasSize(7)
                .containsOnlyKeys("incidentType", "username", "browser", "countryFlag", "ipAddress", "what", "where")
                .containsEntry("incidentType", "Session Expired")
                .containsEntry("username", username)
                .containsEntry("browser", "Mozilla")
                .containsEntry("countryFlag", JbstConstants.Flags.UK)
                .containsEntry("ipAddress", "2.2.2.2")
                .containsEntry("what", "Mozilla, MacOS on Desktop")
                .containsEntry("where", "🇬🇧 UK, London");
    }
}
