package jbst.foundation.incidents.domain.authetication;

import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.domain.http.requests.JbstUserRequestMetadata;
import jbst.foundation.incidents.domain.JbstAbstractIncident;
import jbst.foundation.incidents.domain.JbstIncident;

public record JbstIncidentAuthenticationLoginFailureUsernameMaskedPassword(
        UsernamePasswordCredentials credentials,
        JbstUserRequestMetadata userRequestMetadata
) implements JbstAbstractIncident {

    public static JbstIncidentAuthenticationLoginFailureUsernameMaskedPassword hardcoded() {
        return new JbstIncidentAuthenticationLoginFailureUsernameMaskedPassword(
                UsernamePasswordCredentials.hardcodedMasked(),
                JbstUserRequestMetadata.testData()
        );
    }

    @Override
    public JbstIncident getPlainIncident() {
        return new JbstIncident(
                JbstSecurityJwtIncident.AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD,
                this.credentials,
                this.userRequestMetadata
        );
    }
}
