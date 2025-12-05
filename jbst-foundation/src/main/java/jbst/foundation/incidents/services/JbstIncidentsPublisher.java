package jbst.foundation.incidents.services;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.incidents.domain.JbstIncident;
import jbst.foundation.incidents.domain.authetication.*;
import jbst.foundation.incidents.domain.registration.*;
import jbst.foundation.incidents.domain.session.JbstIncidentSessionExpired;
import jbst.foundation.incidents.domain.session.JbstIncidentSessionRefreshed;
import jbst.foundation.incidents.domain.system.JbstIncidentSystemResetServerCompleted;
import jbst.foundation.incidents.domain.system.JbstIncidentSystemResetServerStarted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import static jbst.foundation.domain.constants.JbstConstants.Logs.USER_ACTION;
import static jbst.foundation.domain.enums.JbstStatus.COMPLETED;
import static jbst.foundation.domain.enums.JbstStatus.STARTED;

@SuppressWarnings("LoggingSimilarMessage")
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstIncidentsPublisher {
    // Spring Publisher
    private final ApplicationEventPublisher applicationEventPublisher;
    // Properties
    private final JbstProperties jbstProperties;

    public void publishThrowable(Throwable throwable) {
        this.publishIncident(new JbstIncident(throwable));
    }

    public void publishIncident(JbstIncident incident) {
        this.applicationEventPublisher.publishEvent(incident);
    }

    public void publishResetServerStarted(JbstIncidentSystemResetServerStarted incident) {
        LOGGER.debug(JbstConstants.Logs.getUserProcess(incident.username(), "Reset Server", STARTED));
        this.applicationEventPublisher.publishEvent(incident);
    }

    public void publishResetServerCompleted(JbstIncidentSystemResetServerCompleted incident) {
        LOGGER.debug(JbstConstants.Logs.getUserProcess(incident.username(), "Reset Server", COMPLETED));
        this.applicationEventPublisher.publishEvent(incident);
    }

    public void publishAuthenticationLogin(JbstIncidentAuthenticationLogin incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("AUTHENTICATION_LOGIN", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.username(), "[pub, incidents] login");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }

    public void publishAuthenticationLoginFailureUsernamePassword(JbstIncidentAuthenticationLoginFailureUsernamePassword incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.credentials().username(), "[pub, incidents] login failure");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }

    public void publishAuthenticationLoginFailureUsernameMaskedPassword(JbstIncidentAuthenticationLoginFailureUsernameMaskedPassword incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.credentials().username(), "[pub, incidents] login failure");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }

    public void publishAuthenticationLogoutMin(JbstIncidentAuthenticationLogoutMin incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("AUTHENTICATION_LOGOUT_MIN", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.username(), "[pub, incidents] logout");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }

    public void publishAuthenticationLogoutFull(JbstIncidentAuthenticationLogoutFull incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("AUTHENTICATION_LOGOUT", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.username(), "[pub, incidents] logout");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }

    public void publishRegistrationMagicLink(JbstIncidentRegistrationMagicLink incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("REGISTER_MAGICLINK", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.username(), "[pub, incidents] register magiclink");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }

    public void publishRegistration0(JbstIncidentRegistration0 incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("REGISTER0", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.username(), "[pub, incidents] register0");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }

    public void publishRegistration0Failure(JbstIncidentRegistration0Failure incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("REGISTER0_FAILURE", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.username(), "[pub, incidents] register0 failure");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }

    public void publishRegistration1(JbstIncidentRegistration1 incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("REGISTER1", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.username(), "[pub, incidents] register1");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }

    public void publishRegistration1Failure(JbstIncidentRegistration1Failure incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("REGISTER1_FAILURE", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.username(), "[pub, incidents] register1 failure");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }

    public void publishSessionRefreshed(JbstIncidentSessionRefreshed incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("SESSION_REFRESHED", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.username(), "[pub, incidents] session refreshed");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }

    public void publishSessionExpired(JbstIncidentSessionExpired incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("SESSION_EXPIRED", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.username(), "[pub, incidents] session expired");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }
}
