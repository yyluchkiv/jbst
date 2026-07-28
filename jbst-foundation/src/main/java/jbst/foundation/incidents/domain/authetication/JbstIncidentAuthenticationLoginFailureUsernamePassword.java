package jbst.foundation.incidents.domain.authetication;

import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.domain.http.requests.JbstUserRequestMetadata;
import jbst.foundation.incidents.domain.JbstAbstractIncident;
import jbst.foundation.incidents.domain.JbstIncident;

public record JbstIncidentAuthenticationLoginFailureUsernamePassword(
        UsernamePasswordCredentials credentials,
        JbstUserRequestMetadata userRequestMetadata
) implements JbstAbstractIncident {

    public static JbstIncidentAuthenticationLoginFailureUsernamePassword fixed() {
        return new JbstIncidentAuthenticationLoginFailureUsernamePassword(
                UsernamePasswordCredentials.fixed(),
                JbstUserRequestMetadata.testData()
        );
    }

    @Override
    public JbstIncident getPlainIncident() {
        return new JbstIncident(
                JbstSecurityJwtIncident.AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD,
                this.credentials,
                this.userRequestMetadata
        );
    }
}
