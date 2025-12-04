package jbst.foundation.incidents.domain.authetication;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.http.requests.JbstUserRequestMetadata;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.incidents.domain.AbstractIncident;
import jbst.foundation.incidents.domain.Incident;

public record IncidentAuthenticationLogoutFull(
        Username username,
        JbstUserRequestMetadata userRequestMetadata
) implements AbstractIncident {

    @Override
    public Incident getPlainIncident() {
        return new Incident(
                JbstSecurityJwtIncident.AUTHENTICATION_LOGOUT,
                this.username,
                this.userRequestMetadata
        );
    }
}
