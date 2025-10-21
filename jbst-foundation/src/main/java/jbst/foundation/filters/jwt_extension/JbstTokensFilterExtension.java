package jbst.foundation.filters.jwt_extension;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.domain.exceptions.tokens.*;
import org.jetbrains.annotations.NotNull;

public interface JbstTokensFilterExtension {
    void doFilter(@NotNull HttpServletRequest request) throws JbstAccessTokenNotFoundException,
            JbstAccessTokenExpiredException,
            JbstRefreshTokenNotFoundException,
            JbstAccessTokenInvalidException,
            JbstRefreshTokenInvalidException,
            JbstAccessTokenDbNotFoundException,
            JbstTokenExtensionUnauthorizedException,
            JbstTokenExtensionAccessDeniedException;
}
