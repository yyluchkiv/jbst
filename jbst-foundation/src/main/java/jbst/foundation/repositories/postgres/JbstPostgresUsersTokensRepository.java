package jbst.foundation.repositories.postgres;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.databases.postgres.entities.JbstPostgresUserToken;
import jbst.foundation.domain.dto.requests.JbstRequestUserToken;
import jbst.foundation.domain.enums.JbstUserTokenType;
import jbst.foundation.domain.ids.JbstTokenId;
import jbst.foundation.repositories.JbstUsersTokensRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.time.JbstTime.getCurrentTimestamp;

public interface JbstPostgresUsersTokensRepository extends JpaRepository<JbstPostgresUserToken, String>, JbstUsersTokensRepository {

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

    @Transactional
    default void cleanupExpired() {
        this.deleteAllByExpiryTimestampBefore(getCurrentTimestamp());
    }

    @Transactional
    default void cleanupUsed() {
        this.deleteAllByUsedIsTrue();
    }

    default JbstTokenId saveAs(JbstUserToken token) {
        var entity = this.save(new JbstPostgresUserToken(token));
        return entity.tokenId();
    }

    default JbstUserToken saveAs(JbstRequestUserToken request) {
        var entity = this.save(
                new JbstPostgresUserToken(
                        request
                )
        );
        return entity.asUserToken();
    }

    // ================================================================================================================
    // Spring Data
    // ================================================================================================================
    JbstPostgresUserToken findByValue(String value);
    JbstPostgresUserToken findByEmailAndTypeAndExpiryTimestampAfterAndUsedIsFalse(
            Email email,
            JbstUserTokenType type,
            long timestamp
    );
    @Transactional
    void deleteAllByExpiryTimestampBefore(long timestamp);
    @Transactional
    void deleteAllByUsedIsTrue();
}
