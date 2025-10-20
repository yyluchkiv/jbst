package jbst.server.iam.base.events.subscribers;

import jbst.foundation.domain.events.EventAuthenticationLogin;
import jbst.foundation.events.publishers.incidents.SecurityJwtIncidentsPublisher;
import jbst.foundation.events.subscribers.events.base.BaseSecurityJwtEventsSubscriber;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import jbst.foundation.services.JbstUsersSessionsService;
import jbst.foundation.services.JbstUsersTokensService;
import jbst.foundation.services.base.UsersEmailsService;
import jbst.foundation.utils.JbstGeoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SecurityJwtEventsSubscriberImpl extends BaseSecurityJwtEventsSubscriber {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public SecurityJwtEventsSubscriberImpl(
            SecurityJwtIncidentsPublisher securityJwtIncidentsPublisher,
            JbstUsersTokensService usersTokensService,
            UsersEmailsService usersEmailsService,
            JbstUsersSessionsService usersSessionsService,
            JbstGeoUtils geoUtils,
            IncidentPublisher incidentPublisher
    ) {
        super(
                securityJwtIncidentsPublisher,
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
