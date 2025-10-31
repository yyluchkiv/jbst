package jbst.foundation.incidents.domain.session;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.http.requests.UserRequestMetadata;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.incidents.domain.AbstractIncident;
import jbst.foundation.incidents.domain.Incident;

public record IncidentSessionRefreshed(
        Username username,
        UserRequestMetadata userRequestMetadata
) implements AbstractIncident {

    @Override
    public Incident getPlainIncident() {
        return new Incident(
                JbstSecurityJwtIncident.SESSION_REFRESHED,
                this.username,
                this.userRequestMetadata
        );
    }
}
