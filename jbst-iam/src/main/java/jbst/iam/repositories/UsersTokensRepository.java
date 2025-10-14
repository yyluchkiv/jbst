package jbst.iam.repositories;

import jbst.iam.domain.db.UserToken;
import jbst.foundation.domain.dto.requests.RequestUserToken;
import jbst.foundation.domain.ids.TokenId;

public interface UsersTokensRepository {
    UserToken findByValueAsAny(String value);
    UserToken findByUserTokenValidOrNull(RequestUserToken request);
    void cleanupExpired();
    void cleanupUsed();
    TokenId saveAs(UserToken token);
    UserToken saveAs(RequestUserToken request);
}
