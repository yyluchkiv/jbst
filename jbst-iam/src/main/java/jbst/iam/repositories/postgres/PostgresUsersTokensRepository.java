package jbst.iam.repositories.postgres;

import jbst.foundation.domain.base.Username;
import jbst.foundation.utilities.time.TimestampUtility;
import jbst.iam.domain.db.UserToken;
import jbst.iam.domain.dto.requests.RequestUserToken;
import jbst.iam.domain.enums.UserTokenType;
import jbst.iam.domain.identifiers.TokenId;
import jbst.iam.domain.postgres.db.PostgresDbUserToken;
import jbst.iam.repositories.UsersTokensRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static java.util.Objects.nonNull;

public interface PostgresUsersTokensRepository extends JpaRepository<PostgresDbUserToken, UUID>, UsersTokensRepository {

    // ================================================================================================================
    // Any
    // ================================================================================================================
    default UserToken findByValueAsAny(String value) {
        var entity = this.findByValue(value);
        return nonNull(entity) ? entity.asUserToken() : null;
    }

    default UserToken findByUsernameValidOrNull(Username username, UserTokenType type) {
        var currentTimestamp = TimestampUtility.getCurrentTimestamp();
        var entity = this.findByUsernameAndTypeAndExpiryTimestampAfterAndUsedIsFalse(username, type, currentTimestamp);
        return nonNull(entity) ? entity.asUserToken() : null;
    }

    @Transactional
    @Modifying
    default void cleanupExpired() {
        this.deleteAllByExpiryTimestampBefore(TimestampUtility.getCurrentTimestamp());
    }

    @Transactional
    @Modifying
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
    PostgresDbUserToken findByUsernameAndTypeAndExpiryTimestampAfterAndUsedIsFalse(
            Username username,
            UserTokenType type,
            long timestamp
    );
    void deleteAllByExpiryTimestampBefore(long timestamp);
    void deleteAllByUsedIsTrue();
}
