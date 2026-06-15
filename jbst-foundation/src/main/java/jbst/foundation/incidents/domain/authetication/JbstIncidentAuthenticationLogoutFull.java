package jbst.foundation.incidents.domain.authetication;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.domain.http.requests.JbstUserRequestMetadata;
import jbst.foundation.incidents.domain.JbstAbstractIncident;
import jbst.foundation.incidents.domain.JbstIncident;

public record JbstIncidentAuthenticationLogoutFull(
        Username username,
        JbstUserRequestMetadata userRequestMetadata
) implements JbstAbstractIncident {

    public static JbstIncidentAuthenticationLogoutFull hardcoded() {
        return new JbstIncidentAuthenticationLogoutFull(
                Username.hardcoded(),
                JbstUserRequestMetadata.invalid()
        );
    }

    @Override
    public JbstIncident getPlainIncident() {
        return new JbstIncident(
                JbstSecurityJwtIncident.AUTHENTICATION_LOGOUT,
                this.username,
                this.userRequestMetadata
        );
    }
}
