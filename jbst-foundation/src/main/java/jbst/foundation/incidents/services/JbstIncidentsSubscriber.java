package jbst.foundation.incidents.services;

import jbst.foundation.incidents.domain.JbstIncident;
import jbst.foundation.incidents.domain.authetication.*;
import jbst.foundation.incidents.domain.registration.*;
import jbst.foundation.incidents.domain.session.JbstIncidentSessionExpired;
import jbst.foundation.incidents.domain.session.JbstIncidentSessionRefreshed;
import jbst.foundation.incidents.domain.system.JbstIncidentSystemResetServerCompleted;
import jbst.foundation.incidents.domain.system.JbstIncidentSystemResetServerStarted;
import jbst.foundation.incidents.feigns.clients.JbstIncidentClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import static jbst.foundation.domain.constants.JbstConstants.Logs.USER_ACTION;

@SuppressWarnings("LoggingSimilarMessage")
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstIncidentsSubscriber {
    // Clients
    private final JbstIncidentClient incidentClient;

    @EventListener
    public void onEvent(JbstIncident incident) {
        this.incidentClient.registerIncident(incident);
    }

    @EventListener
    public void onEvent(JbstIncidentSystemResetServerStarted incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[sub, incidents] system reset server started");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(JbstIncidentSystemResetServerCompleted incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[sub, incidents] system reset server completed");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(JbstIncidentAuthenticationLogin incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[sub, incidents] login");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(JbstIncidentAuthenticationLoginFailureUsernamePassword incident) {
        LOGGER.debug(USER_ACTION, incident.credentials().username(), "[sub, incidents] login failure");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(JbstIncidentAuthenticationLoginFailureUsernameMaskedPassword incident) {
        LOGGER.debug(USER_ACTION, incident.credentials().username(), "[sub, incidents] login failure");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(JbstIncidentAuthenticationLogoutMin incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[sub, incidents] logout");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(JbstIncidentAuthenticationLogoutFull incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[sub, incidents] logout");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(JbstIncidentRegistrationMagicLink incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[sub, incidents] register-magiclink");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(JbstIncidentRegistration0 incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[sub, incidents] register0");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(JbstIncidentRegistration0Failure incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[sub, incidents] register0 failure");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(JbstIncidentRegistration1 incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[sub, incidents] register1");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(JbstIncidentRegistration1Failure incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[sub, incidents] register1 failure");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(JbstIncidentSessionRefreshed incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[sub, incidents] session refreshed");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(JbstIncidentSessionExpired incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[pub, incidents] session expired");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }
}
