package jbst.foundation.incidents.events.publishers.impl;

import jbst.foundation.incidents.domain.Incident;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IncidentPublisherImpl implements IncidentPublisher {

    // Spring Publisher
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishIncident(Incident incident) {
        this.applicationEventPublisher.publishEvent(incident);
    }
}
