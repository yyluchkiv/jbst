package jbst.foundation.incidents.domain.authetication;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.http.requests.JbstUserRequestMetadata;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.incidents.domain.JbstAbstractIncident;
import jbst.foundation.incidents.domain.JbstIncident;

public record JbstIncidentAuthenticationLogin(
        Username username,
        JbstUserRequestMetadata userRequestMetadata
) implements JbstAbstractIncident {

    @Override
    public JbstIncident getPlainIncident() {
        return new JbstIncident(
                JbstSecurityJwtIncident.AUTHENTICATION_LOGIN,
                this.username,
                this.userRequestMetadata
        );
    }
}
