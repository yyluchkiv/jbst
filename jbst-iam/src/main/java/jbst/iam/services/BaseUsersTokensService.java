package jbst.iam.services;

import jbst.foundation.domain.exceptions.tokens.UserEmailConfirmException;
import jbst.iam.domain.db.UserToken;
import jbst.iam.domain.dto.requests.RequestUserToken;

public interface BaseUsersTokensService {
    void confirmEmail(String token) throws UserEmailConfirmException;
    UserToken saveAs(RequestUserToken request);
    UserToken getOrCreate(RequestUserToken request);
}
