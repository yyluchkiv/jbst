package jbst.foundation.services;

import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.RequestUserToken;
import jbst.foundation.domain.exceptions.tokens.JbstUserEmailConfirmException;

public interface BaseUsersTokensService {
    void confirmEmail(String token) throws JbstUserEmailConfirmException;
    JbstUserToken saveAs(RequestUserToken request);
    JbstUserToken findOrCreate(RequestUserToken request);
}
