package jbst.foundation.incidents.domain.registration;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.incidents.domain.JbstAbstractIncident;
import jbst.foundation.incidents.domain.JbstIncident;

import static jbst.foundation.incidents.domain.JbstIncident.Constants.Keys.EMAIL;
import static jbst.foundation.incidents.domain.JbstIncident.Constants.Keys.EXCEPTION;

public record JbstIncidentRegistration0Failure(
        Email email,
        Username username,
        String exception
) implements JbstAbstractIncident {

    @Override
    public JbstIncident getPlainIncident() {
        var incident = new JbstIncident(
                JbstSecurityJwtIncident.REGISTER0_FAILURE,
                this.username
        );
        incident.add(EMAIL, this.email);
        incident.add(EXCEPTION, this.exception);
        return incident;
    }
}
