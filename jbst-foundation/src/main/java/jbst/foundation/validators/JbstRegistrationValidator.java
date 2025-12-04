package jbst.foundation.validators;

import jbst.foundation.domain.dto.requests.JbstRequestUserRegistration0;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistration1;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistrationMagicLink;
import jbst.foundation.domain.exceptions.JbstExceptions;

public interface JbstRegistrationValidator {
    void validateRegistrationRequestMagicLink(JbstRequestUserRegistrationMagicLink request);
    void validateRegistrationRequest0(JbstRequestUserRegistration0 request) throws JbstExceptions.Registration;
    void validateRegistrationRequest1(JbstRequestUserRegistration1 request) throws JbstExceptions.Registration;
}
