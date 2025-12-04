package jbst.foundation.incidents.domain.authetication;

import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.http.requests.JbstUserRequestMetadata;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.incidents.domain.AbstractIncident;
import jbst.foundation.incidents.domain.Incident;

public record IncidentAuthenticationLoginFailureUsernamePassword(
        UsernamePasswordCredentials credentials,
        JbstUserRequestMetadata userRequestMetadata
) implements AbstractIncident {

    @Override
    public Incident getPlainIncident() {
        return new Incident(
                JbstSecurityJwtIncident.AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD,
                this.credentials,
                this.userRequestMetadata
        );
    }
}
