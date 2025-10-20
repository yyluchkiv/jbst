package jbst.foundation.incidents.domain.registration;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.properties.base.JbstIamIncidentType;
import jbst.foundation.incidents.domain.AbstractIncident;
import jbst.foundation.incidents.domain.Incident;

public record IncidentRegistrationMagicLink(
        Email email
) implements AbstractIncident {

    @Override
    public Incident getPlainIncident() {
        return new Incident(
                JbstIamIncidentType.REGISTER_MAGICLINK,
                new Username(this.email.value())
        );
    }
}
