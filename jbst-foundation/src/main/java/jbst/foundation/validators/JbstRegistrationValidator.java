package jbst.foundation.validators;

import jbst.foundation.domain.dto.requests.RequestUserRegistration0;
import jbst.foundation.domain.dto.requests.RequestUserRegistration1;
import jbst.foundation.domain.dto.requests.RequestUserRegistrationMagicLink;
import jbst.foundation.domain.exceptions.JbstExceptions;

public interface JbstRegistrationValidator {
    void validateRegistrationRequestMagicLink(RequestUserRegistrationMagicLink request);
    void validateRegistrationRequest0(RequestUserRegistration0 request) throws JbstExceptions.Registration;
    void validateRegistrationRequest1(RequestUserRegistration1 request) throws JbstExceptions.Registration;
}
