package jbst.foundation.incidents.domain.registration;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistrationMagicLink;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.incidents.domain.JbstAbstractIncident;
import jbst.foundation.incidents.domain.JbstIncident;

public record JbstIncidentRegistrationMagicLink(
        Username username
) implements JbstAbstractIncident {

    public static JbstIncidentRegistrationMagicLink of(JbstRequestUserRegistrationMagicLink request) {
        return new JbstIncidentRegistrationMagicLink(new Username(request.email().value()));
    }

    @Override
    public JbstIncident getPlainIncident() {
        return new JbstIncident(
                JbstSecurityJwtIncident.REGISTER_MAGICLINK,
                this.username
        );
    }
}
