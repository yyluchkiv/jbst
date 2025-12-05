package jbst.foundation.incidents.domain.session;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.http.requests.JbstUserRequestMetadata;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.incidents.domain.JbstAbstractIncident;
import jbst.foundation.incidents.domain.JbstIncident;

public record JbstIncidentSessionRefreshed(
        Username username,
        JbstUserRequestMetadata userRequestMetadata
) implements JbstAbstractIncident {

    @Override
    public JbstIncident getPlainIncident() {
        return new JbstIncident(
                JbstSecurityJwtIncident.SESSION_REFRESHED,
                this.username,
                this.userRequestMetadata
        );
    }
}
