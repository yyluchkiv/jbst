package jbst.iam.assistants.current;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.iam.domain.dto.responses.ResponseUserSessionsTable;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.iam.domain.security.CurrentClientUser;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.exceptions.tokens.AccessTokenNotFoundException;

public interface CurrentSessionAssistant {
    Username getCurrentUsername();
    JwtUser getCurrentJwtUser();
    CurrentClientUser getCurrentClientUser();
    JbstUserSession getCurrentUserSession(HttpServletRequest httpServletRequest) throws AccessTokenNotFoundException;
    ResponseUserSessionsTable getCurrentUserDbSessionsTable(RequestAccessToken requestAccessToken);
}
