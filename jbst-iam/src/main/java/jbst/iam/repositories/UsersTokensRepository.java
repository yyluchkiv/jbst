package jbst.iam.repositories;

import jbst.iam.domain.db.UserToken;
import jbst.iam.domain.dto.requests.RequestUserToken;
import jbst.iam.domain.identifiers.TokenId;

public interface UsersTokensRepository {
    UserToken findByValueAsAny(String value);
    UserToken findByUserTokenValidOrNull(RequestUserToken request);
    void cleanupExpired();
    void cleanupUsed();
    TokenId saveAs(UserToken token);
    UserToken saveAs(RequestUserToken request);
}
