package jbst.server.iam.base.events.subscribers;

import jbst.foundation.domain.events.EventAuthenticationLogin;
import jbst.foundation.events.publishers.incidents.SecurityJwtIncidentsPublisher;
import jbst.foundation.events.subscribers.events.base.BaseSecurityJwtEventsSubscriber;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import jbst.foundation.services.BaseUsersSessionsService;
import jbst.foundation.services.BaseUsersTokensService;
import jbst.foundation.services.UsersEmailsService;
import jbst.foundation.utils.UserMetadataUtils;
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
            BaseUsersTokensService baseUsersTokensService,
            UsersEmailsService usersEmailsService,
            BaseUsersSessionsService baseUsersSessionsService,
            UserMetadataUtils userMetadataUtils,
            IncidentPublisher incidentPublisher
    ) {
        super(
                securityJwtIncidentsPublisher,
                baseUsersTokensService,
                usersEmailsService,
                baseUsersSessionsService,
                userMetadataUtils,
                incidentPublisher
        );
    }

    @Override
    public void onAuthenticationLogin(EventAuthenticationLogin event) {
        super.onAuthenticationLogin(event);
        LOGGER.info("[Server] SecurityJwtSubscriber.onAuthenticationLogin(). Username: {}", event.username());
    }
}
