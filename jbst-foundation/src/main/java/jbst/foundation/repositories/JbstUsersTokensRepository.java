package jbst.foundation.repositories;

import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.JbstRequestUserToken;
import jbst.foundation.domain.ids.JbstTokenId;

import static java.util.Objects.nonNull;

public interface JbstUsersTokensRepository {
    JbstUserToken findByValueAsAnyOrNull(String value);
    JbstUserToken findByUserTokenValidOrNull(JbstRequestUserToken request);
    void cleanupExpired();
    void cleanupUsed();
    JbstTokenId saveAs(JbstUserToken token);
    JbstUserToken saveAs(JbstRequestUserToken request);

    default JbstUserToken findOrCreate(JbstRequestUserToken request) {
        var userToken = this.findByUserTokenValidOrNull(request);
        if (nonNull(userToken)) {
            return userToken;
        } else {
            return this.saveAs(request);
        }
    }
}
