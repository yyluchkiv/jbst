package jbst.foundation.incidents.domain.system;

import jbst.foundation.domain.base.Username;
import jbst.foundation.incidents.domain.JbstAbstractIncident;
import jbst.foundation.incidents.domain.JbstIncident;

public record JbstIncidentSystemResetServerCompleted(
        Username username
) implements JbstAbstractIncident {

    public static JbstIncidentSystemResetServerCompleted hardcoded() {
        return new JbstIncidentSystemResetServerCompleted(
                Username.hardcoded()
        );
    }

    @Override
    public JbstIncident getPlainIncident() {
        var incident = new JbstIncident("Reset Server Completed");
        incident.addUsername(this.username);
        return incident;
    }
}
