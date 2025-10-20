package jbst.foundation.events.publishers.incidents;

import jbst.foundation.incidents.domain.authetication.*;
import jbst.foundation.incidents.domain.registration.*;
import jbst.foundation.incidents.domain.session.IncidentSessionExpired;
import jbst.foundation.incidents.domain.session.IncidentSessionRefreshed;

public interface SecurityJwtIncidentsPublisher {
    void publishAuthenticationLogin(IncidentAuthenticationLogin incident);
    void publishAuthenticationLoginFailureUsernamePassword(IncidentAuthenticationLoginFailureUsernamePassword incident);
    void publishAuthenticationLoginFailureUsernameMaskedPassword(IncidentAuthenticationLoginFailureUsernameMaskedPassword incident);
    void publishAuthenticationLogoutMin(IncidentAuthenticationLogoutMin incident);
    void publishAuthenticationLogoutFull(IncidentAuthenticationLogoutFull incident);
    void publishRegistrationMagicLink(IncidentRegistrationMagicLink incident);
    void publishRegistration0(IncidentRegistration0 incident);
    void publishRegistration0Failure(IncidentRegistration0Failure incident);
    void publishRegistration1(IncidentRegistration1 incident);
    void publishRegistration1Failure(IncidentRegistration1Failure incident);
    void publishSessionRefreshed(IncidentSessionRefreshed incident);
    void publishSessionExpired(IncidentSessionExpired incident);
}
