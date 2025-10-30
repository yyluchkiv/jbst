package jbst.foundation.events.subscribers;

import jbst.foundation.incidents.domain.authetication.*;
import jbst.foundation.incidents.domain.registration.*;
import jbst.foundation.incidents.domain.session.IncidentSessionExpired;
import jbst.foundation.incidents.domain.session.IncidentSessionRefreshed;
import jbst.foundation.incidents.domain.system.IncidentSystemResetServerCompleted;
import jbst.foundation.incidents.domain.system.IncidentSystemResetServerStarted;
import jbst.foundation.incidents.feigns.clients.IncidentClient;
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
    private final IncidentClient incidentClient;

    @EventListener
    public void onEvent(IncidentSystemResetServerStarted incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[sub, incidents] system reset server started");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(IncidentSystemResetServerCompleted incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[sub, incidents] system reset server completed");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(IncidentAuthenticationLogin incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[sub, incidents] login");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(IncidentAuthenticationLoginFailureUsernamePassword incident) {
        LOGGER.debug(USER_ACTION, incident.credentials().username(), "[sub, incidents] login failure");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(IncidentAuthenticationLoginFailureUsernameMaskedPassword incident) {
        LOGGER.debug(USER_ACTION, incident.credentials().username(), "[sub, incidents] login failure");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(IncidentAuthenticationLogoutMin incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[sub, incidents] logout");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(IncidentAuthenticationLogoutFull incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[sub, incidents] logout");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(IncidentRegistrationMagicLink incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[sub, incidents] register-magiclink");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(IncidentRegistration0 incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[sub, incidents] register0");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(IncidentRegistration0Failure incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[sub, incidents] register0 failure");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(IncidentRegistration1 incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[sub, incidents] register1");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(IncidentRegistration1Failure incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[sub, incidents] register1 failure");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(IncidentSessionRefreshed incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[sub, incidents] session refreshed");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }

    @EventListener
    public void onEvent(IncidentSessionExpired incident) {
        LOGGER.debug(USER_ACTION, incident.username(), "[pub, incidents] session expired");
        this.incidentClient.registerIncident(incident.getPlainIncident());
    }
}
