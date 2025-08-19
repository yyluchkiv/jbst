package jbst.iam.repositories.postgres;

import jbst.foundation.domain.base.Email;
import jbst.iam.domain.db.UserToken;
import jbst.iam.domain.dto.requests.RequestUserToken;
import jbst.iam.domain.enums.UserTokenType;
import jbst.iam.domain.identifiers.TokenId;
import jbst.iam.domain.postgres.db.PostgresDbUserToken;
import jbst.iam.repositories.UsersTokensRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.nonNull;
import static jbst.foundation.utilities.time.TimestampUtility.getCurrentTimestamp;

public interface PostgresUsersTokensRepository extends JpaRepository<PostgresDbUserToken, String>, UsersTokensRepository {

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

    @Transactional
    default void cleanupExpired() {
        this.deleteAllByExpiryTimestampBefore(getCurrentTimestamp());
    }

    @Transactional
    default void cleanupUsed() {
        this.deleteAllByUsedIsTrue();
    }

    default TokenId saveAs(UserToken token) {
        var entity = this.save(new PostgresDbUserToken(token));
        return entity.tokenId();
    }

    default UserToken saveAs(RequestUserToken request) {
        var entity = this.save(
                new PostgresDbUserToken(
                        request
                )
        );
        return entity.asUserToken();
    }

    // ================================================================================================================
    // Spring Data
    // ================================================================================================================
    PostgresDbUserToken findByValue(String value);
    PostgresDbUserToken findByEmailAndTypeAndExpiryTimestampAfterAndUsedIsFalse(
            Email email,
            UserTokenType type,
            long timestamp
    );
    @Transactional
    void deleteAllByExpiryTimestampBefore(long timestamp);
    @Transactional
    void deleteAllByUsedIsTrue();
}
