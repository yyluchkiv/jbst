package jbst.foundation.repositories.postgres;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.databases.postgres.entities.PostgresDbUserToken;
import jbst.foundation.domain.dto.requests.RequestUserToken;
import jbst.foundation.domain.enums.UserTokenType;
import jbst.foundation.domain.ids.TokenId;
import jbst.foundation.repositories.JbstUsersTokensRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.nonNull;
import static jbst.foundation.utilities.time.TimestampUtility.getCurrentTimestamp;

public interface PostgresJbstUsersTokensRepository extends JpaRepository<PostgresDbUserToken, String>, JbstUsersTokensRepository {

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

    @Transactional
    default void cleanupExpired() {
        this.deleteAllByExpiryTimestampBefore(getCurrentTimestamp());
    }

    @Transactional
    default void cleanupUsed() {
        this.deleteAllByUsedIsTrue();
    }

    default TokenId saveAs(JbstUserToken token) {
        var entity = this.save(new PostgresDbUserToken(token));
        return entity.tokenId();
    }

    default JbstUserToken saveAs(RequestUserToken request) {
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
