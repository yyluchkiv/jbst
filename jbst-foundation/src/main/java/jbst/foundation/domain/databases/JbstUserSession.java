package jbst.foundation.domain.databases;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.http.requests.JbstUserRequestMetadata;
import jbst.foundation.domain.ids.JbstUserSessionId;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.jwt.JbstJwtRefreshToken;

import static jbst.foundation.domain.random.JbstRandomEntities.entity;
import static jbst.foundation.domain.random.JbstRandom.randomBoolean;
import static jbst.foundation.domain.time.JbstTime.getCurrentTimestamp;

public record JbstUserSession(
        boolean persisted,
        JbstUserSessionId id,
        long createdAt,
        long updatedAt,
        Username username,
        JbstJwtAccessToken accessToken,
        JbstJwtRefreshToken refreshToken,
        JbstUserRequestMetadata metadata,
        boolean metadataRenewCron,
        boolean metadataRenewManually
) {

    public static JbstUserSession randomPersistedSession() {
        return JbstUserSession.ofPersisted(
                JbstUserSessionId.random(),
                getCurrentTimestamp(),
                getCurrentTimestamp(),
                Username.random(),
                JbstJwtAccessToken.random(),
                JbstJwtRefreshToken.random(),
                JbstUserRequestMetadata.random(),
                randomBoolean(),
                randomBoolean()
        );
    }

    public static JbstUserSession randomNotPersistedSession() {
        return JbstUserSession.ofNotPersisted(
                Username.random(),
                JbstJwtAccessToken.random(),
                JbstJwtRefreshToken.random(),
                JbstUserRequestMetadata.random()
        );
    }

    public static JbstUserSession ofPersisted(
            JbstUserSessionId id,
            long createdAt,
            long updatedAt,
            Username username,
            JbstJwtAccessToken accessToken,
            JbstJwtRefreshToken refreshToken,
            JbstUserRequestMetadata metadata,
            boolean metadataRenewCron,
            boolean metadataRenewManually
    ) {
        return new JbstUserSession(
                true,
                id,
                createdAt,
                updatedAt,
                username,
                accessToken,
                refreshToken,
                metadata,
                metadataRenewCron,
                metadataRenewManually
        );
    }

    public static JbstUserSession ofNotPersisted(
            Username username,
            JbstJwtAccessToken accessToken,
            JbstJwtRefreshToken refreshToken,
            JbstUserRequestMetadata metadata
    ) {
        var currentTimestamp = getCurrentTimestamp();
        return new JbstUserSession(
                false,
                JbstUserSessionId.undefined(),
                currentTimestamp,
                currentTimestamp,
                username,
                accessToken,
                refreshToken,
                metadata,
                false,
                false
        );
    }

    public static JbstUserSession random(Username owner, JbstJwtAccessToken accessToken, JbstJwtRefreshToken refreshToken) {
        return JbstUserSession.ofPersisted(
                JbstUserSessionId.random(),
                getCurrentTimestamp(),
                getCurrentTimestamp(),
                owner,
                accessToken,
                refreshToken,
                JbstUserRequestMetadata.random(),
                false,
                false
        );
    }

    public static JbstUserSession random(String owner, String accessToken, String refreshToken) {
        return random(
                Username.of(owner),
                JbstJwtAccessToken.of(accessToken),
                JbstJwtRefreshToken.of(refreshToken)
        );
    }

    public static JbstUserSession random(Username owner, String accessToken) {
        return random(owner.value(), accessToken, entity(JbstJwtRefreshToken.class).value());
    }

    public static JbstUserSession random(String owner) {
        return random(Username.of(owner), entity(JbstJwtAccessToken.class).value());
    }

    public boolean isRenewRequired() {
        return this.metadataRenewCron || this.metadataRenewManually;
    }
}
