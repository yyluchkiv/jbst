package jbst.foundation.incidents.domain.authetication;

import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.http.requests.UserRequestMetadata;
import jbst.foundation.domain.enums.JbstIncidentType;
import jbst.foundation.incidents.domain.AbstractIncident;
import jbst.foundation.incidents.domain.Incident;

public record IncidentAuthenticationLoginFailureUsernameMaskedPassword(
        UsernamePasswordCredentials credentials,
        UserRequestMetadata userRequestMetadata
) implements AbstractIncident {

    @Override
    public Incident getPlainIncident() {
        return new Incident(
                JbstIncidentType.AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD,
                this.credentials,
                this.userRequestMetadata
        );
    }
}
