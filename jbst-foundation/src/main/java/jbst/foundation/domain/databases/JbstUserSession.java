package jbst.foundation.domain.databases;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.http.requests.UserRequestMetadata;
import jbst.foundation.domain.ids.UserSessionId;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;

import static jbst.foundation.domain.random.JbstRandomEntities.entity;
import static jbst.foundation.domain.random.JbstRandom.randomBoolean;
import static jbst.foundation.utilities.time.TimestampUtility.getCurrentTimestamp;

public record JbstUserSession(
        boolean persisted,
        UserSessionId id,
        long createdAt,
        long updatedAt,
        Username username,
        JwtAccessToken accessToken,
        JwtRefreshToken refreshToken,
        UserRequestMetadata metadata,
        boolean metadataRenewCron,
        boolean metadataRenewManually
) {

    public static JbstUserSession randomPersistedSession() {
        return JbstUserSession.ofPersisted(
                UserSessionId.random(),
                getCurrentTimestamp(),
                getCurrentTimestamp(),
                Username.random(),
                JwtAccessToken.random(),
                JwtRefreshToken.random(),
                UserRequestMetadata.random(),
                randomBoolean(),
                randomBoolean()
        );
    }

    public static JbstUserSession randomNotPersistedSession() {
        return JbstUserSession.ofNotPersisted(
                Username.random(),
                JwtAccessToken.random(),
                JwtRefreshToken.random(),
                UserRequestMetadata.random()
        );
    }

    public static JbstUserSession ofPersisted(
            UserSessionId id,
            long createdAt,
            long updatedAt,
            Username username,
            JwtAccessToken accessToken,
            JwtRefreshToken refreshToken,
            UserRequestMetadata metadata,
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
            JwtAccessToken accessToken,
            JwtRefreshToken refreshToken,
            UserRequestMetadata metadata
    ) {
        var currentTimestamp = getCurrentTimestamp();
        return new JbstUserSession(
                false,
                UserSessionId.undefined(),
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

    public static JbstUserSession random(Username owner, JwtAccessToken accessToken, JwtRefreshToken refreshToken) {
        return JbstUserSession.ofPersisted(
                UserSessionId.random(),
                getCurrentTimestamp(),
                getCurrentTimestamp(),
                owner,
                accessToken,
                refreshToken,
                UserRequestMetadata.random(),
                false,
                false
        );
    }

    public static JbstUserSession random(String owner, String accessToken, String refreshToken) {
        return random(
                Username.of(owner),
                JwtAccessToken.of(accessToken),
                JwtRefreshToken.of(refreshToken)
        );
    }

    public static JbstUserSession random(Username owner, String accessToken) {
        return random(owner.value(), accessToken, entity(JwtRefreshToken.class).value());
    }

    public static JbstUserSession random(String owner) {
        return random(Username.of(owner), entity(JwtAccessToken.class).value());
    }

    public boolean isRenewRequired() {
        return this.metadataRenewCron || this.metadataRenewManually;
    }
}
