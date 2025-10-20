package jbst.foundation.events.subscribers.events.base;

import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.events.*;
import jbst.foundation.domain.functions.FunctionAccountAccessed;
import jbst.foundation.domain.http.requests.UserRequestMetadata;
import jbst.foundation.domain.pubsub.AbstractEventSubscriber;
import jbst.foundation.events.publishers.incidents.SecurityJwtIncidentsPublisher;
import jbst.foundation.events.subscribers.events.SecurityJwtEventsSubscriber;
import jbst.foundation.incidents.domain.authetication.IncidentAuthenticationLogin;
import jbst.foundation.incidents.domain.authetication.IncidentAuthenticationLoginFailureUsernameMaskedPassword;
import jbst.foundation.incidents.domain.authetication.IncidentAuthenticationLoginFailureUsernamePassword;
import jbst.foundation.incidents.domain.session.IncidentSessionRefreshed;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import jbst.foundation.services.BaseUsersSessionsService;
import jbst.foundation.services.BaseUsersTokensService;
import jbst.foundation.services.UsersEmailsService;
import jbst.foundation.utils.JbstGeoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.Objects.isNull;
import static jbst.foundation.domain.constants.JbstConstants.Logs.USER_ACTION;

@SuppressWarnings("LoggingSimilarMessage")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BaseSecurityJwtEventsSubscriber extends AbstractEventSubscriber implements SecurityJwtEventsSubscriber {

    // Publishers
    private final SecurityJwtIncidentsPublisher securityJwtIncidentsPublisher;
    // Services
    private final BaseUsersTokensService baseUsersTokensService;
    private final UsersEmailsService usersEmailsService;
    private final BaseUsersSessionsService baseUsersSessionsService;
    // Utils
    private final JbstGeoUtils geoUtils;
    // Incidents
    private final IncidentPublisher incidentPublisher;

    @Override
    public void onAuthenticationLoginMagicLinkFailure(EventAuthenticationMagicLinkFailure event) {
        LOGGER.debug(USER_ACTION, event.token(), "[sub, events] login magiclink");
    }

    @Override
    public void onAuthenticationLogin(EventAuthenticationLogin event) {
        LOGGER.debug(USER_ACTION, event.username(), "[sub, events] login");
    }

    @Override
    public void onAuthenticationLoginFailure(EventAuthenticationLoginFailure event) {
        try {
            LOGGER.debug(USER_ACTION, event.username(), "[sub, events] login failure");
            var userRequestMetadata = this.geoUtils.getUserRequestMetadataProcessed(
                    event.ipAddress(),
                    event.userAgentHeader()
            );
            this.securityJwtIncidentsPublisher.publishAuthenticationLoginFailureUsernamePassword(
                    new IncidentAuthenticationLoginFailureUsernamePassword(
                            new UsernamePasswordCredentials(
                                    event.username(),
                                    event.password()
                            ),
                            userRequestMetadata
                    )
            );
            this.securityJwtIncidentsPublisher.publishAuthenticationLoginFailureUsernameMaskedPassword(
                    new IncidentAuthenticationLoginFailureUsernameMaskedPassword(
                            UsernamePasswordCredentials.mask5(
                                    event.username(),
                                    event.password()
                            ),
                            userRequestMetadata
                    )
            );
        } catch (RuntimeException ex) {
            this.incidentPublisher.publishThrowable(ex);
        }
    }

    @Override
    public void onAuthenticationLogout(EventAuthenticationLogout event) {
        LOGGER.debug(USER_ACTION, event.username(), "[sub, events] logout");
    }

    @Override
    public void onRegistrationMagicLink(EventRegistrationMagicLink event) {
        LOGGER.debug(USER_ACTION, event.request().email(), "[sub, events] register magiclink");
    }

    @Override
    public void onRegistration0(EventRegistration0 event) {
        try {
            LOGGER.debug(USER_ACTION, event.requestUserRegistration0().username(), "[sub, events] register0");
            var userToken = this.baseUsersTokensService.saveAs(event.requestUserRegistration0().asRequestUserToken());
            this.usersEmailsService.executeEmailConfirmation(userToken);
        } catch (RuntimeException ex) {
            this.incidentPublisher.publishThrowable(ex);
        }
    }

    @Override
    public void onRegistration0Failure(EventRegistration0Failure event) {
        LOGGER.debug(USER_ACTION, event.username(), "[sub, events] register0 failure");
    }

    @Override
    public void onRegistration1(EventRegistration1 event) {
        LOGGER.debug(USER_ACTION, event.requestUserRegistration1().username(), "[sub, events] register1");
    }

    @Override
    public void onRegistration1Failure(EventRegistration1Failure event) {
        LOGGER.debug(USER_ACTION, event.username(), "[sub, events] register1 failure");
    }

    @Override
    public void onSessionRefreshed(EventSessionRefreshed event) {
        LOGGER.debug(USER_ACTION, event.session().username(), "[sub, events] session refreshed");
    }

    @Override
    public void onSessionExpired(EventSessionExpired event) {
        LOGGER.debug(USER_ACTION, event.session().username(), "[sub, events] session expired");
    }

    @Override
    public void onSessionUserRequestMetadataAdd(EventSessionUserRequestMetadataAdd event) {
        try {
            LOGGER.debug(USER_ACTION, event.username(), "[sub, events] session user request metadata add");
            var session = this.baseUsersSessionsService.saveUserRequestMetadata(event);
            var metadata = session.metadata();
            this.processSessionUserRequestMetadataAddEmails(event, metadata);
            this.processSessionUserRequestMetadataAddIncidents(event, metadata);
        } catch (RuntimeException ex) {
            this.incidentPublisher.publishThrowable(ex);
        }
    }

    @Override
    public void onSessionUserRequestMetadataRenew(EventSessionUserRequestMetadataRenew event) {
        try {
            LOGGER.debug(USER_ACTION, event.username(), "[sub, events] session user request metadata renew, sessionId: " + event.session().id());
            this.baseUsersSessionsService.saveUserRequestMetadata(event);
        } catch (RuntimeException ex) {
            this.incidentPublisher.publishThrowable(ex);
        }
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private void processSessionUserRequestMetadataAddEmails(
            EventSessionUserRequestMetadataAdd event,
            UserRequestMetadata metadata
    ) {
        if (isNull(event.email())) {
            return;
        }
        this.usersEmailsService.executeAccountAccessed(
                new FunctionAccountAccessed(
                        event.username(),
                        event.email(),
                        metadata,
                        event.accountAccessMethod()
                )
        );
    }

    private void processSessionUserRequestMetadataAddIncidents(
            EventSessionUserRequestMetadataAdd event,
            UserRequestMetadata metadata
    ) {
        if (event.isUsernamePassword()) {
            this.securityJwtIncidentsPublisher.publishAuthenticationLogin(
                    new IncidentAuthenticationLogin(
                            event.username(),
                            metadata
                    )
            );
        }
        if (event.isSessionToken()) {
            this.securityJwtIncidentsPublisher.publishSessionRefreshed(
                    new IncidentSessionRefreshed(
                            event.username(),
                            metadata
                    )
            );
        }
    }
}
