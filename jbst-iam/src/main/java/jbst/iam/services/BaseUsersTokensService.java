package jbst.iam.services;

import jbst.foundation.domain.exceptions.tokens.UserEmailConfirmException;
import jbst.iam.domain.db.UserToken;
import jbst.iam.domain.dto.requests.RequestUserRegistrationMagicLink;
import jbst.iam.domain.dto.requests.RequestUserToken;

public interface BaseUsersTokensService {
    UserToken magicLink(RequestUserRegistrationMagicLink request);
    void confirmEmail(String token) throws UserEmailConfirmException;
    UserToken saveAs(RequestUserToken request);
    UserToken getOrCreate(RequestUserToken request);
}
