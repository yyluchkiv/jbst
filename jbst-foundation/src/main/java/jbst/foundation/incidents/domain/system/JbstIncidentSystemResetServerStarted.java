package jbst.foundation.incidents.domain.system;

import jbst.foundation.domain.base.Username;
import jbst.foundation.incidents.domain.JbstAbstractIncident;
import jbst.foundation.incidents.domain.JbstIncident;

public record JbstIncidentSystemResetServerStarted(
        Username username
) implements JbstAbstractIncident {

    public static JbstIncidentSystemResetServerStarted fixed() {
        return new JbstIncidentSystemResetServerStarted(
                Username.fixed()
        );
    }

    @Override
    public JbstIncident getPlainIncident() {
        var incident = new JbstIncident("Reset Server Started");
        incident.addUsername(this.username);
        return incident;
    }
}
