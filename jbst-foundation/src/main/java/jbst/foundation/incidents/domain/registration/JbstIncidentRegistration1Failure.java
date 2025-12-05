package jbst.foundation.incidents.domain.registration;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.incidents.domain.JbstAbstractIncident;
import jbst.foundation.incidents.domain.JbstIncident;

import static jbst.foundation.incidents.domain.JbstIncident.Constants.Keys.*;

public record JbstIncidentRegistration1Failure(
        Username username,
        String code,
        Username invitationOwner,
        String exception
) implements JbstAbstractIncident {

    public static JbstIncidentRegistration1Failure of(
            Username username,
            String code,
            String exception
    ) {
        return new JbstIncidentRegistration1Failure(
                username,
                code,
                Username.dash(),
                exception
        );
    }

    @Override
    public JbstIncident getPlainIncident() {
        var incident = new JbstIncident(
                JbstSecurityJwtIncident.REGISTER1_FAILURE,
                this.username
        );
        incident.add(EXCEPTION, this.exception);
        incident.add(INVITATION_CODE, this.code);
        incident.add(INVITATION_OWNER, this.invitationOwner);
        return incident;
    }
}
