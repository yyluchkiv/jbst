package jbst.foundation.events.subscribers.incidents;

import jbst.foundation.incidents.domain.authetication.*;
import jbst.foundation.incidents.domain.registration.*;
import jbst.foundation.incidents.domain.session.IncidentSessionExpired;
import jbst.foundation.incidents.domain.session.IncidentSessionRefreshed;
import org.springframework.context.event.EventListener;

public interface SecurityJwtIncidentsSubscriber {
    @EventListener
    void onEvent(IncidentAuthenticationLogin incident);
    @EventListener
    void onEvent(IncidentAuthenticationLoginFailureUsernamePassword incident);
    @EventListener
    void onEvent(IncidentAuthenticationLoginFailureUsernameMaskedPassword incident);
    @EventListener
    void onEvent(IncidentAuthenticationLogoutMin incident);
    @EventListener
    void onEvent(IncidentAuthenticationLogoutFull incident);
    @EventListener
    void onEvent(IncidentRegistrationMagicLink incident);
    @EventListener
    void onEvent(IncidentRegistration0 incident);
    @EventListener
    void onEvent(IncidentRegistration0Failure incident);
    @EventListener
    void onEvent(IncidentRegistration1 incident);
    @EventListener
    void onEvent(IncidentRegistration1Failure incident);
    @EventListener
    void onEvent(IncidentSessionRefreshed incident);
    @EventListener
    void onEvent(IncidentSessionExpired incident);
}
