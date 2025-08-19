package jbst.iam.services;

import jbst.iam.domain.dto.requests.RequestUserRegistration0;
import jbst.iam.domain.dto.requests.RequestUserRegistration1;
import jbst.iam.domain.dto.requests.RequestUserRegistrationMagicLink;
import jbst.iam.domain.exceptions.LoginException;

public interface BaseRegistrationService {
    void registerMagicLink(RequestUserRegistrationMagicLink request);
    void register0(RequestUserRegistration0 request);
    void register1(RequestUserRegistration1 request);
}
