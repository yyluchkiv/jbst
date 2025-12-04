package jbst.foundation.events.subscribers;

import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.events.*;
import jbst.foundation.domain.functions.JbstFunctionAccountAccessed;
import jbst.foundation.domain.http.requests.UserRequestMetadata;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.incidents.domain.authetication.IncidentAuthenticationLogin;
import jbst.foundation.incidents.domain.authetication.IncidentAuthenticationLoginFailureUsernameMaskedPassword;
import jbst.foundation.incidents.domain.authetication.IncidentAuthenticationLoginFailureUsernamePassword;
import jbst.foundation.incidents.domain.session.IncidentSessionRefreshed;
import jbst.foundation.services.JbstUsersSessionsService;
import jbst.foundation.services.JbstUsersTokensService;
import jbst.foundation.services.base.JbstUsersEmailsService;
import jbst.foundation.utils.JbstGeoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import static java.util.Objects.isNull;
import static jbst.foundation.domain.constants.JbstConstants.Logs.USER_ACTION;

@SuppressWarnings("LoggingSimilarMessage")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstEventsSubscriber {
    // Publishers
    private final JbstIncidentsPublisher incidentsPublisher;
    // Services
    private final JbstUsersTokensService usersTokensService;
    private final JbstUsersEmailsService usersEmailsService;
    private final JbstUsersSessionsService usersSessionsService;
    // Utils
    private final JbstGeoUtils geoUtils;

    @EventListener
    public void onAuthenticationLoginMagicLinkFailure(JbstEventAuthenticationMagicLinkFailure event) {
        LOGGER.debug(USER_ACTION, event.token(), "[sub, events] login magiclink");
    }

    @EventListener
    public void onAuthenticationLogin(JbstEventAuthenticationLogin event) {
        LOGGER.debug(USER_ACTION, event.username(), "[sub, events] login");
    }

    @EventListener
    public void onAuthenticationLoginFailure(JbstEventAuthenticationLoginFailure event) {
        try {
            LOGGER.debug(USER_ACTION, event.username(), "[sub, events] login failure");
            var userRequestMetadata = this.geoUtils.getUserRequestMetadataProcessed(
                    event.ipAddress(),
                    event.userAgentHeader()
            );
            this.incidentsPublisher.publishAuthenticationLoginFailureUsernamePassword(
                    new IncidentAuthenticationLoginFailureUsernamePassword(
                            new UsernamePasswordCredentials(
                                    event.username(),
                                    event.password()
                            ),
                            userRequestMetadata
                    )
            );
            this.incidentsPublisher.publishAuthenticationLoginFailureUsernameMaskedPassword(
                    new IncidentAuthenticationLoginFailureUsernameMaskedPassword(
                            UsernamePasswordCredentials.mask5(
                                    event.username(),
                                    event.password()
                            ),
                            userRequestMetadata
                    )
            );
        } catch (RuntimeException ex) {
            this.incidentsPublisher.publishThrowable(ex);
        }
    }

    @EventListener
    public void onAuthenticationLogout(JbstEventAuthenticationLogout event) {
        LOGGER.debug(USER_ACTION, event.username(), "[sub, events] logout");
    }

    @EventListener
    public void onRegistrationMagicLink(JbstEventRegistrationMagicLink event) {
        LOGGER.debug(USER_ACTION, event.request().email(), "[sub, events] register magiclink");
    }

    @EventListener
    public void onRegistration0(JbstEventRegistration0 event) {
        try {
            LOGGER.debug(USER_ACTION, event.requestUserRegistration0().username(), "[sub, events] register0");
            var userToken = this.usersTokensService.saveAs(event.requestUserRegistration0().asRequestUserToken());
            this.usersEmailsService.executeEmailConfirmation(userToken);
        } catch (RuntimeException ex) {
            this.incidentsPublisher.publishThrowable(ex);
        }
    }

    @EventListener
    public void onRegistration0Failure(JbstEventRegistration0Failure event) {
        LOGGER.debug(USER_ACTION, event.username(), "[sub, events] register0 failure");
    }

    @EventListener
    public void onRegistration1(JbstEventRegistration1 event) {
        LOGGER.debug(USER_ACTION, event.requestUserRegistration1().username(), "[sub, events] register1");
    }

    @EventListener
    public void onRegistration1Failure(JbstEventRegistration1Failure event) {
        LOGGER.debug(USER_ACTION, event.username(), "[sub, events] register1 failure");
    }

    @EventListener
    public void onSessionRefreshed(JbstEventSessionRefreshed event) {
        LOGGER.debug(USER_ACTION, event.session().username(), "[sub, events] session refreshed");
    }

    @EventListener
    public void onSessionExpired(JbstEventSessionExpired event) {
        LOGGER.debug(USER_ACTION, event.session().username(), "[sub, events] session expired");
    }

    @EventListener
    public void onSessionUserRequestMetadataAdd(JbstEventSessionUserRequestMetadataAdd event) {
        try {
            LOGGER.debug(USER_ACTION, event.username(), "[sub, events] session user request metadata add");
            var session = this.usersSessionsService.saveUserRequestMetadata(event);
            var metadata = session.metadata();
            this.processSessionUserRequestMetadataAddEmails(event, metadata);
            this.processSessionUserRequestMetadataAddIncidents(event, metadata);
        } catch (RuntimeException ex) {
            this.incidentsPublisher.publishThrowable(ex);
        }
    }

    @EventListener
    public void onSessionUserRequestMetadataRenew(JbstEventSessionUserRequestMetadataRenew event) {
        try {
            LOGGER.debug(USER_ACTION, event.username(), "[sub, events] session user request metadata renew, sessionId: " + event.session().id());
            this.usersSessionsService.saveUserRequestMetadata(event);
        } catch (RuntimeException ex) {
            this.incidentsPublisher.publishThrowable(ex);
        }
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private void processSessionUserRequestMetadataAddEmails(
            JbstEventSessionUserRequestMetadataAdd event,
            UserRequestMetadata metadata
    ) {
        if (isNull(event.email())) {
            return;
        }
        this.usersEmailsService.executeAccountAccessed(
                new JbstFunctionAccountAccessed(
                        event.username(),
                        event.email(),
                        metadata,
                        event.accountAccessMethod()
                )
        );
    }

    private void processSessionUserRequestMetadataAddIncidents(
            JbstEventSessionUserRequestMetadataAdd event,
            UserRequestMetadata metadata
    ) {
        if (event.isUsernamePassword()) {
            this.incidentsPublisher.publishAuthenticationLogin(
                    new IncidentAuthenticationLogin(
                            event.username(),
                            metadata
                    )
            );
        }
        if (event.isSessionToken()) {
            this.incidentsPublisher.publishSessionRefreshed(
                    new IncidentSessionRefreshed(
                            event.username(),
                            metadata
                    )
            );
        }
    }
}
