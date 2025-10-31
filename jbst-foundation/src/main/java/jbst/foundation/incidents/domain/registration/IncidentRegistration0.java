package jbst.foundation.incidents.domain.registration;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.incidents.domain.AbstractIncident;
import jbst.foundation.incidents.domain.Incident;

public record IncidentRegistration0(
        Username username
) implements AbstractIncident {

    @Override
    public Incident getPlainIncident() {
        return new Incident(
                JbstSecurityJwtIncident.REGISTER0,
                this.username
        );
    }
}
