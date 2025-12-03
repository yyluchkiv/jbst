package jbst.foundation.assistants.current;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.responses.ResponseUserSessionsTable;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.security.CurrentClientUser;

public interface CurrentSessionAssistant {
    Username getCurrentUsername();
    JwtUser getCurrentJwtUser();
    CurrentClientUser getCurrentClientUser();
    JbstUserSession getCurrentUserSession(HttpServletRequest httpServletRequest) throws JbstExceptions.AccessTokenNotFound;
    ResponseUserSessionsTable getCurrentUserDbSessionsTable(RequestAccessToken requestAccessToken);
}
