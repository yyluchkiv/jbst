package jbst.foundation.incidents.domain.registration;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistrationMagicLink;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.incidents.domain.AbstractIncident;
import jbst.foundation.incidents.domain.Incident;

public record IncidentRegistrationMagicLink(
        Username username
) implements AbstractIncident {

    public static IncidentRegistrationMagicLink of(JbstRequestUserRegistrationMagicLink request) {
        return new IncidentRegistrationMagicLink(new Username(request.email().value()));
    }

    @Override
    public Incident getPlainIncident() {
        return new Incident(
                JbstSecurityJwtIncident.REGISTER_MAGICLINK,
                this.username
        );
    }
}
