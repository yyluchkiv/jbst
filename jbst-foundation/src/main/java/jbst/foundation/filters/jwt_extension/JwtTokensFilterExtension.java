package jbst.foundation.filters.jwt_extension;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.domain.exceptions.tokens.*;
import org.jetbrains.annotations.NotNull;

public interface JwtTokensFilterExtension {
    void doFilter(@NotNull HttpServletRequest request) throws AccessTokenNotFoundException,
            AccessTokenExpiredException,
            RefreshTokenNotFoundException,
            AccessTokenInvalidException,
            RefreshTokenInvalidException,
            AccessTokenDbNotFoundException,
            TokenExtensionUnauthorizedException,
            TokenExtensionAccessDeniedException;
}
