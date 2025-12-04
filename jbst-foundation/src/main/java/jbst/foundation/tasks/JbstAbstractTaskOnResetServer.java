package jbst.foundation.tasks;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.jwt.JbstJwtUser;
import jbst.foundation.domain.system.JbstSystemResetServerStatus;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static jbst.foundation.domain.enums.JbstStatus.COMPLETED;
import static jbst.foundation.domain.enums.JbstStatus.STARTED;

@Slf4j
@AllArgsConstructor
public abstract class JbstAbstractTaskOnResetServer {

    protected final JbstIncidentsPublisher incidentsPublisher;

    public abstract JbstSystemResetServerStatus getStatus();
    public abstract void resetOnServer(JbstJwtUser initiator);

    public final void reset(JbstJwtUser initiator) {
        if (this.getStatus().getState().isResetting()) {
            return;
        }
        LOGGER.info(JbstConstants.Logs.getUserProcess(initiator.username(), "Reset Server", STARTED));

        try {
            this.resetOnServer(initiator);
        } catch (RuntimeException ex) {
            this.incidentsPublisher.publishThrowable(ex);
        }

        LOGGER.info(JbstConstants.Logs.getUserProcess(initiator.username(), "Reset Server", COMPLETED));
    }
}
