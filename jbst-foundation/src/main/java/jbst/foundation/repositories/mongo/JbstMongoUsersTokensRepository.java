package jbst.foundation.repositories.mongo;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.databases.mongo.JbstMongoUserToken;
import jbst.foundation.domain.dto.requests.JbstRequestUserToken;
import jbst.foundation.domain.enums.JbstUserTokenType;
import jbst.foundation.domain.ids.JbstTokenId;
import jbst.foundation.repositories.JbstUsersTokensRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.time.JbstTime.getCurrentTimestamp;

public interface JbstMongoUsersTokensRepository extends MongoRepository<JbstMongoUserToken, String>, JbstUsersTokensRepository {

    // ================================================================================================================
    // Any
    // ================================================================================================================
    default JbstUserToken findByValueAsAnyOrNull(String value) {
        var entity = this.findByValue(value);
        return nonNull(entity) ? entity.asUserToken() : null;
    }

    default JbstUserToken findByUserTokenValidOrNull(JbstRequestUserToken request) {
        var entity = this.findByEmailAndTypeAndExpiryTimestampAfterAndUsedIsFalse(
                request.email(),
                request.type(),
                getCurrentTimestamp()
        );
        return nonNull(entity) ? entity.asUserToken() : null;
    }

    default void cleanupExpired() {
        this.deleteAllByExpiryTimestampBefore(getCurrentTimestamp());
    }

    default void cleanupUsed() {
        this.deleteAllByUsedIsTrue();
    }

    default JbstTokenId saveAs(JbstUserToken token) {
        var entity = this.save(new JbstMongoUserToken(token));
        return entity.tokenId();
    }

    default JbstUserToken saveAs(JbstRequestUserToken request) {
        var entity = this.save(
                new JbstMongoUserToken(
                        request
                )
        );
        return entity.asUserToken();
    }

    // ================================================================================================================
    // Spring Data
    // ================================================================================================================
    JbstMongoUserToken findByValue(String value);
    JbstMongoUserToken findByEmailAndTypeAndExpiryTimestampAfterAndUsedIsFalse(
            Email email,
            JbstUserTokenType type,
            long timestamp
    );
    void deleteAllByExpiryTimestampBefore(long timestamp);
    void deleteAllByUsedIsTrue();
}
