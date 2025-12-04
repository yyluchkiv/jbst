package jbst.foundation.services;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.dto.requests.JbstRequestAccessToken;
import jbst.foundation.domain.events.JbstEventSessionUserRequestMetadataAdd;
import jbst.foundation.domain.events.JbstEventSessionUserRequestMetadataRenew;
import jbst.foundation.domain.functions.JbstFunctionSessionUserRequestMetadataSave;
import jbst.foundation.domain.ids.JbstUserSessionId;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.jwt.JbstJwtRefreshToken;
import jbst.foundation.domain.jwt.JbstJwtUser;
import jbst.foundation.domain.sessions.JbstSessionsExpiredTable;

import java.util.Set;

public interface JbstUsersSessionsService {
    void assertAccess(Username username, JbstUserSessionId sessionId);
    void save(JbstJwtUser user, JbstJwtAccessToken accessToken, JbstJwtRefreshToken refreshToken, HttpServletRequest httpServletRequest);
    void refresh(JbstJwtUser user, JbstUserSession oldSession, JbstJwtAccessToken newAccessToken, JbstJwtRefreshToken newRefreshToken, HttpServletRequest httpServletRequest);
    JbstUserSession saveUserRequestMetadata(JbstEventSessionUserRequestMetadataAdd event);
    void saveUserRequestMetadata(JbstEventSessionUserRequestMetadataRenew event);
    JbstUserSession saveUserRequestMetadata(JbstFunctionSessionUserRequestMetadataSave saveFunction);
    JbstSessionsExpiredTable getExpiredRefreshTokensSessions(Set<Username> usernames);
    void enableUserRequestMetadataRenewCron();
    void enableUserRequestMetadataRenewManually(JbstUserSessionId sessionId);
    void renewUserRequestMetadata(JbstUserSession session, HttpServletRequest httpServletRequest);
    void deleteById(JbstUserSessionId sessionId);
    void deleteAllExceptCurrent(Username username, JbstRequestAccessToken requestAccessToken);
    void deleteAllExceptCurrentAsSuperuser(JbstRequestAccessToken requestAccessToken);
}
