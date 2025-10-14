package jbst.iam.services;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.events.EventSessionUserRequestMetadataAdd;
import jbst.foundation.domain.events.EventSessionUserRequestMetadataRenew;
import jbst.foundation.domain.functions.FunctionSessionUserRequestMetadataSave;
import jbst.foundation.domain.ids.UserSessionId;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.sessions.SessionsExpiredTable;
import jbst.foundation.domain.base.Username;

import java.util.Set;

public interface BaseUsersSessionsService {
    void assertAccess(Username username, UserSessionId sessionId);
    void save(JwtUser user, JwtAccessToken accessToken, JwtRefreshToken refreshToken, HttpServletRequest httpServletRequest);
    void refresh(JwtUser user, JbstUserSession oldSession, JwtAccessToken newAccessToken, JwtRefreshToken newRefreshToken, HttpServletRequest httpServletRequest);
    JbstUserSession saveUserRequestMetadata(EventSessionUserRequestMetadataAdd event);
    void saveUserRequestMetadata(EventSessionUserRequestMetadataRenew event);
    JbstUserSession saveUserRequestMetadata(FunctionSessionUserRequestMetadataSave saveFunction);
    SessionsExpiredTable getExpiredRefreshTokensSessions(Set<Username> usernames);
    void enableUserRequestMetadataRenewCron();
    void enableUserRequestMetadataRenewManually(UserSessionId sessionId);
    void renewUserRequestMetadata(JbstUserSession session, HttpServletRequest httpServletRequest);
    void deleteById(UserSessionId sessionId);
    void deleteAllExceptCurrent(Username username, RequestAccessToken requestAccessToken);
    void deleteAllExceptCurrentAsSuperuser(RequestAccessToken requestAccessToken);
}
