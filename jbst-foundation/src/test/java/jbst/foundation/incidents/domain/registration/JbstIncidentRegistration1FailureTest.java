package jbst.foundation.incidents.domain.registration;

import jbst.foundation.domain.base.Username;
import org.junit.jupiter.api.Test;

import static jbst.foundation.domain.random.JbstRandom.randomString;
import static org.assertj.core.api.Assertions.assertThat;

class JbstIncidentRegistration1FailureTest {

    @Test
    void convertRegister1FailureIncidentTest() {
        // Arrange
        var username = Username.fixed();
        var exception = randomString();
        var code = randomString();
        var incident = JbstIncidentRegistration1Failure.of(
                username,
                code,
                exception
        );

        // Act
        var actual = incident.getPlainIncident();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getType()).isEqualTo("Register1 Failure");
        assertThat(actual.getUsername().value()).isEqualTo("jbst");
        assertThat(actual.getAttributes())
                .hasSize(5)
                .containsOnlyKeys("incidentType", "username", "exception", "invitationCode", "invitationOwner")
                .containsEntry("incidentType", "Register1 Failure")
                .containsEntry("username", username)
                .containsEntry("exception", exception)
                .containsEntry("invitationCode", code)
                .containsEntry("invitationOwner", Username.dash());
    }
}
