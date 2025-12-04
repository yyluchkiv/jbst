package jbst.foundation.events.publishers;

import jbst.foundation.domain.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import static jbst.foundation.domain.constants.JbstConstants.Logs.USER_ACTION;

@SuppressWarnings("LoggingSimilarMessage")
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstEventsPublisher {
    // Spring Publisher
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishAuthenticationLoginMagicLinkFailure(JbstEventAuthenticationMagicLinkFailure event) {
        LOGGER.debug(USER_ACTION, event.token(), "[pub, events] login magiclink failure");
        this.applicationEventPublisher.publishEvent(event);
    }

    public void publishAuthenticationLogin(JbstEventAuthenticationLogin event) {
        LOGGER.debug(USER_ACTION, event.username(), "[pub, events] login");
        this.applicationEventPublisher.publishEvent(event);
    }

    public void publishAuthenticationLoginFailure(JbstEventAuthenticationLoginFailure event) {
        LOGGER.debug(USER_ACTION, event.username(), "[pub, events] login failure");
        this.applicationEventPublisher.publishEvent(event);
    }

    public void publishAuthenticationLogout(JbstEventAuthenticationLogout event) {
        LOGGER.debug(USER_ACTION, event.username(), "[pub, events] logout");
        this.applicationEventPublisher.publishEvent(event);
    }

    public void publishRegistrationMagicLink(JbstEventRegistrationMagicLink event) {
        LOGGER.debug(USER_ACTION, event.request().email(), "[pub, events] register magiclink");
        this.applicationEventPublisher.publishEvent(event);
    }

    public void publishRegistration0(JbstEventRegistration0 event) {
        LOGGER.debug(USER_ACTION, event.requestUserRegistration0().username(), "[pub, events] register0");
        this.applicationEventPublisher.publishEvent(event);
    }

    public void publishRegistration0Failure(JbstEventRegistration0Failure event) {
        LOGGER.debug(USER_ACTION, event.username(), "[pub, events] register0 failure");
        this.applicationEventPublisher.publishEvent(event);
    }

    public void publishRegistration1(JbstEventRegistration1 event) {
        LOGGER.debug(USER_ACTION, event.requestUserRegistration1().username(), "[pub, events] register1");
        this.applicationEventPublisher.publishEvent(event);
    }

    public void publishRegistration1Failure(JbstEventRegistration1Failure event) {
        LOGGER.debug(USER_ACTION, event.username(), "[pub, events] register1 failure");
        this.applicationEventPublisher.publishEvent(event);
    }

    public void publishSessionRefreshed(JbstEventSessionRefreshed event) {
        LOGGER.debug(USER_ACTION, event.session().username(), "[pub, events] session refreshed");
        this.applicationEventPublisher.publishEvent(event);
    }

    public void publishSessionExpired(JbstEventSessionExpired event) {
        LOGGER.debug(USER_ACTION, event.session().username(), "[pub, events] session expired");
        this.applicationEventPublisher.publishEvent(event);
    }

    public void publishSessionUserRequestMetadataAdd(JbstEventSessionUserRequestMetadataAdd event) {
        LOGGER.debug(USER_ACTION, event.username(), "[pub, events] session user request metadata add");
        this.applicationEventPublisher.publishEvent(event);
    }

    public void publishSessionUserRequestMetadataRenew(JbstEventSessionUserRequestMetadataRenew event) {
        LOGGER.debug(USER_ACTION, event.username(), "[pub, events] session user request metadata renew, sessionId: " + event.session().id());
        this.applicationEventPublisher.publishEvent(event);
    }
}
