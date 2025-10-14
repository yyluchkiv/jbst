package jbst.foundation.services;

import jbst.foundation.domain.dto.requests.RequestUserRegistration0;
import jbst.foundation.domain.dto.requests.RequestUserRegistration1;

public interface BaseRegistrationService {
    void register0(RequestUserRegistration0 request);
    void register1(RequestUserRegistration1 request);
}
