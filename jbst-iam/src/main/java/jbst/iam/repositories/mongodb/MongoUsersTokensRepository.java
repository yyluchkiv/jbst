package jbst.iam.repositories.mongodb;

import jbst.foundation.domain.base.Email;
import jbst.iam.domain.db.UserToken;
import jbst.iam.domain.dto.requests.RequestUserToken;
import jbst.iam.domain.enums.UserTokenType;
import jbst.iam.domain.identifiers.TokenId;
import jbst.iam.domain.mongodb.MongoDbUserToken;
import jbst.iam.repositories.UsersTokensRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import static java.util.Objects.nonNull;
import static jbst.foundation.utilities.time.TimestampUtility.getCurrentTimestamp;

public interface MongoUsersTokensRepository extends MongoRepository<MongoDbUserToken, String>, UsersTokensRepository {

    // ================================================================================================================
    // Any
    // ================================================================================================================
    default UserToken findByValueAsAny(String value) {
        var entity = this.findByValue(value);
        return nonNull(entity) ? entity.asUserToken() : null;
    }

    default UserToken findByEmailValidOrNull(RequestUserToken request) {
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

    default TokenId saveAs(UserToken token) {
        var entity = this.save(new MongoDbUserToken(token));
        return entity.tokenId();
    }

    default UserToken saveAs(RequestUserToken request) {
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
            UserTokenType type,
            long timestamp
    );
    void deleteAllByExpiryTimestampBefore(long timestamp);
    void deleteAllByUsedIsTrue();
}
