package jbst.foundation.incidents.events.publishers.impl;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.incidents.domain.Incident;
import jbst.foundation.incidents.domain.system.IncidentSystemResetServerCompleted;
import jbst.foundation.incidents.domain.system.IncidentSystemResetServerStarted;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import static jbst.foundation.domain.enums.Status.COMPLETED;
import static jbst.foundation.domain.enums.Status.STARTED;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IncidentPublisherImpl implements IncidentPublisher {

    // Spring Publisher
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishResetServerStarted(IncidentSystemResetServerStarted incident) {
        LOGGER.debug(JbstConstants.Logs.getUserProcess(incident.username(), "Reset Server", STARTED));
        this.applicationEventPublisher.publishEvent(incident);
    }

    @Override
    public void publishResetServerCompleted(IncidentSystemResetServerCompleted incident) {
        LOGGER.debug(JbstConstants.Logs.getUserProcess(incident.username(), "Reset Server", COMPLETED));
        this.applicationEventPublisher.publishEvent(incident);
    }

    @Override
    public void publishIncident(Incident incident) {
        this.applicationEventPublisher.publishEvent(incident);
    }
}
