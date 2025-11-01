package jbst.foundation.incidents.services;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.incidents.domain.Incident;
import jbst.foundation.incidents.domain.authetication.*;
import jbst.foundation.incidents.domain.registration.*;
import jbst.foundation.incidents.domain.session.IncidentSessionExpired;
import jbst.foundation.incidents.domain.session.IncidentSessionRefreshed;
import jbst.foundation.incidents.domain.system.IncidentSystemResetServerCompleted;
import jbst.foundation.incidents.domain.system.IncidentSystemResetServerStarted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import static jbst.foundation.domain.constants.JbstConstants.Logs.USER_ACTION;
import static jbst.foundation.domain.enums.Status.COMPLETED;
import static jbst.foundation.domain.enums.Status.STARTED;

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
        this.publishIncident(new Incident(throwable));
    }

    public void publishIncident(Incident incident) {
        this.applicationEventPublisher.publishEvent(incident);
    }

    public void publishResetServerStarted(IncidentSystemResetServerStarted incident) {
        LOGGER.debug(JbstConstants.Logs.getUserProcess(incident.username(), "Reset Server", STARTED));
        this.applicationEventPublisher.publishEvent(incident);
    }

    public void publishResetServerCompleted(IncidentSystemResetServerCompleted incident) {
        LOGGER.debug(JbstConstants.Logs.getUserProcess(incident.username(), "Reset Server", COMPLETED));
        this.applicationEventPublisher.publishEvent(incident);
    }

    public void publishAuthenticationLogin(IncidentAuthenticationLogin incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("AUTHENTICATION_LOGIN", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.username(), "[pub, incidents] login");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }

    public void publishAuthenticationLoginFailureUsernamePassword(IncidentAuthenticationLoginFailureUsernamePassword incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.credentials().username(), "[pub, incidents] login failure");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }

    public void publishAuthenticationLoginFailureUsernameMaskedPassword(IncidentAuthenticationLoginFailureUsernameMaskedPassword incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.credentials().username(), "[pub, incidents] login failure");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }

    public void publishAuthenticationLogoutMin(IncidentAuthenticationLogoutMin incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("AUTHENTICATION_LOGOUT_MIN", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.username(), "[pub, incidents] logout");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }

    public void publishAuthenticationLogoutFull(IncidentAuthenticationLogoutFull incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("AUTHENTICATION_LOGOUT", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.username(), "[pub, incidents] logout");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }

    public void publishRegistrationMagicLink(IncidentRegistrationMagicLink incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("REGISTER_MAGICLINK", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.username(), "[pub, incidents] register magiclink");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }

    public void publishRegistration0(IncidentRegistration0 incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("REGISTER0", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.username(), "[pub, incidents] register0");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }

    public void publishRegistration0Failure(IncidentRegistration0Failure incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("REGISTER0_FAILURE", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.username(), "[pub, incidents] register0 failure");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }

    public void publishRegistration1(IncidentRegistration1 incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("REGISTER1", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.username(), "[pub, incidents] register1");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }

    public void publishRegistration1Failure(IncidentRegistration1Failure incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("REGISTER1_FAILURE", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.username(), "[pub, incidents] register1 failure");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }

    public void publishSessionRefreshed(IncidentSessionRefreshed incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("SESSION_REFRESHED", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.username(), "[pub, incidents] session refreshed");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }

    public void publishSessionExpired(IncidentSessionExpired incident) {
        if (this.jbstProperties.getIncidentsManager().isEnabled("SESSION_EXPIRED", JbstSecurityJwtIncident.class)) {
            LOGGER.debug(USER_ACTION, incident.username(), "[pub, incidents] session expired");
            this.applicationEventPublisher.publishEvent(incident);
        }
    }
}
