package jbst.foundation.services;

import jbst.foundation.domain.dto.requests.JbstRequestUserRegistration0;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistration1;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistrationMagicLink;

public interface JbstRegistrationService {
    void registerMagicLink(JbstRequestUserRegistrationMagicLink request);
    void register0(JbstRequestUserRegistration0 request);
    void register1(JbstRequestUserRegistration1 request);
}
