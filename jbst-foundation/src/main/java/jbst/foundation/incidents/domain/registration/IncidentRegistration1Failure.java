package jbst.foundation.incidents.domain.registration;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.enums.JbstIncidentType;
import jbst.foundation.incidents.domain.AbstractIncident;
import jbst.foundation.incidents.domain.Incident;

import static jbst.foundation.incidents.domain.IncidentAttributes.Keys.*;

public record IncidentRegistration1Failure(
        Username username,
        String code,
        Username invitationOwner,
        String exception
) implements AbstractIncident {

    public static IncidentRegistration1Failure of(
            Username username,
            String code,
            String exception
    ) {
        return new IncidentRegistration1Failure(
                username,
                code,
                Username.dash(),
                exception
        );
    }

    @Override
    public Incident getPlainIncident() {
        var incident = new Incident(
                JbstIncidentType.REGISTER1_FAILURE,
                this.username
        );
        incident.add(EXCEPTION, this.exception);
        incident.add(INVITATION_CODE, this.code);
        incident.add(INVITATION_OWNER, this.invitationOwner);
        return incident;
    }
}
