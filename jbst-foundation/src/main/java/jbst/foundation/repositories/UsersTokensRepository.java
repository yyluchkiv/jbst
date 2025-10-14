package jbst.foundation.repositories;

import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.RequestUserToken;
import jbst.foundation.domain.ids.TokenId;

public interface UsersTokensRepository {
    JbstUserToken findByValueAsAny(String value);
    JbstUserToken findByUserTokenValidOrNull(RequestUserToken request);
    void cleanupExpired();
    void cleanupUsed();
    TokenId saveAs(JbstUserToken token);
    JbstUserToken saveAs(RequestUserToken request);
}
