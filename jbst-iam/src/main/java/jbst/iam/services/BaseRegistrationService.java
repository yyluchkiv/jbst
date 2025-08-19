package jbst.iam.services;

import jbst.iam.domain.dto.requests.RequestUserRegistration0;
import jbst.iam.domain.dto.requests.RequestUserRegistration1;

public interface BaseRegistrationService {
    void register0(RequestUserRegistration0 request);
    void register1(RequestUserRegistration1 request);
}
