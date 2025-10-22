package jbst.server.iam.base.events.subscribers;

import jbst.foundation.domain.events.EventAuthenticationLogin;
import jbst.foundation.events.publishers.JbstIncidentsPublisher;
import jbst.foundation.events.subscribers.JbstEventsSubscriber;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import jbst.foundation.services.JbstUsersSessionsService;
import jbst.foundation.services.JbstUsersTokensService;
import jbst.foundation.services.base.JbstUsersEmailsService;
import jbst.foundation.utils.JbstGeoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServerJbstEventsSubscriber extends JbstEventsSubscriber {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public ServerJbstEventsSubscriber(
            JbstIncidentsPublisher incidentsPublisher,
            JbstUsersTokensService usersTokensService,
            JbstUsersEmailsService usersEmailsService,
            JbstUsersSessionsService usersSessionsService,
            JbstGeoUtils geoUtils,
            IncidentPublisher incidentPublisher
    ) {
        super(
                incidentsPublisher,
                usersTokensService,
                usersEmailsService,
                usersSessionsService,
                geoUtils,
                incidentPublisher
        );
    }

    @Override
    public void onAuthenticationLogin(EventAuthenticationLogin event) {
        super.onAuthenticationLogin(event);
        LOGGER.info("[Server] SecurityJwtSubscriber.onAuthenticationLogin(). Username: {}", event.username());
    }
}
