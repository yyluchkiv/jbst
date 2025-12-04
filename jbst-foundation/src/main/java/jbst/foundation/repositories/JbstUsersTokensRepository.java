package jbst.foundation.repositories;

import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.RequestUserToken;
import jbst.foundation.domain.ids.JbstTokenId;

import static java.util.Objects.nonNull;

public interface JbstUsersTokensRepository {
    JbstUserToken findByValueAsAnyOrNull(String value);
    JbstUserToken findByUserTokenValidOrNull(RequestUserToken request);
    void cleanupExpired();
    void cleanupUsed();
    JbstTokenId saveAs(JbstUserToken token);
    JbstUserToken saveAs(RequestUserToken request);

    default JbstUserToken findOrCreate(RequestUserToken request) {
        var userToken = this.findByUserTokenValidOrNull(request);
        if (nonNull(userToken)) {
            return userToken;
        } else {
            return this.saveAs(request);
        }
    }
}
