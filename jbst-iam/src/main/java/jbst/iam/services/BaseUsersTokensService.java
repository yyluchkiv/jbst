package jbst.iam.services;

import jbst.foundation.domain.exceptions.tokens.UserEmailConfirmException;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.RequestUserToken;

public interface BaseUsersTokensService {
    void confirmEmail(String token) throws UserEmailConfirmException;
    JbstUserToken saveAs(RequestUserToken request);
    JbstUserToken getOrCreate(RequestUserToken request);
}
