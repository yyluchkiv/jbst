package jbst.foundation.extension;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.exceptions.JbstExceptions;
import org.jetbrains.annotations.NotNull;

public interface JbstExtensionService {
    // =================================================================================================================
    // HTTP
    // =================================================================================================================
    void doFilter(@NotNull HttpServletRequest request) throws
            JbstExceptions.AccessTokenNotFound,
            JbstExceptions.AccessTokenExpired,
            JbstExceptions.RefreshTokenNotFound,
            JbstExceptions.AccessTokenInvalid,
            JbstExceptions.RefreshTokenInvalid,
            JbstExceptions.AccessTokenDbNotFound,
            JbstExceptions.ExtensionTokenUnauthorized,
            JbstExceptions.ExtensionTokenAccessDenied;
    // =================================================================================================================
    // Resources
    // =================================================================================================================
    void authenticateAsStandard(Username username, HttpServletRequest request, HttpServletResponse response);
    void authenticateAsMagicLink(Username username, HttpServletRequest request, HttpServletResponse response);
    void registerMagicLink(Email email);
    void register0(Username username);
    void register1(Username username);
}
