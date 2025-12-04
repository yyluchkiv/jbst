package jbst.foundation.repositories.mongo;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.databases.mongo.MongoDbUserToken;
import jbst.foundation.domain.dto.requests.RequestUserToken;
import jbst.foundation.domain.enums.JbstUserTokenType;
import jbst.foundation.domain.ids.JbstTokenId;
import jbst.foundation.repositories.JbstUsersTokensRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.time.TimestampUtility.getCurrentTimestamp;

public interface MongoJbstUsersTokensRepository extends MongoRepository<MongoDbUserToken, String>, JbstUsersTokensRepository {

    // ================================================================================================================
    // Any
    // ================================================================================================================
    default JbstUserToken findByValueAsAnyOrNull(String value) {
        var entity = this.findByValue(value);
        return nonNull(entity) ? entity.asUserToken() : null;
    }

    default JbstUserToken findByUserTokenValidOrNull(RequestUserToken request) {
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
        var entity = this.save(new MongoDbUserToken(token));
        return entity.tokenId();
    }

    default JbstUserToken saveAs(RequestUserToken request) {
        var entity = this.save(
                new MongoDbUserToken(
                        request
                )
        );
        return entity.asUserToken();
    }

    // ================================================================================================================
    // Spring Data
    // ================================================================================================================
    MongoDbUserToken findByValue(String value);
    MongoDbUserToken findByEmailAndTypeAndExpiryTimestampAfterAndUsedIsFalse(
            Email email,
            JbstUserTokenType type,
            long timestamp
    );
    void deleteAllByExpiryTimestampBefore(long timestamp);
    void deleteAllByUsedIsTrue();
}
