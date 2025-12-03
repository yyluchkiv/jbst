package jbst.foundation.services;

import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.RequestUserToken;
import jbst.foundation.domain.exceptions.JbstExceptions;

public interface JbstUsersTokensService {
    void confirmEmail(String token) throws JbstExceptions.UserEmailConfirmation;
    JbstUserToken saveAs(RequestUserToken request);
    JbstUserToken findOrCreate(RequestUserToken request);
}
