package jbst.foundation.incidents.domain.authetication;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.http.requests.UserRequestMetadata;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.incidents.domain.AbstractIncident;
import jbst.foundation.incidents.domain.Incident;

public record IncidentAuthenticationLogin(
        Username username,
        UserRequestMetadata userRequestMetadata
) implements AbstractIncident {

    @Override
    public Incident getPlainIncident() {
        return new Incident(
                JbstSecurityJwtIncident.AUTHENTICATION_LOGIN,
                this.username,
                this.userRequestMetadata
        );
    }
}
