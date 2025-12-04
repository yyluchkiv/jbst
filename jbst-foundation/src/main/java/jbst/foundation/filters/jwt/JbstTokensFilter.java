package jbst.foundation.filters.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.sessions.JbstSession;
import jbst.foundation.extension.JbstExtensionService;
import jbst.foundation.handlers.JbstAccessDeniedHandler;
import jbst.foundation.services.base.JbstTokensService;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.tokens.facade.JbstTokensProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstTokensFilter extends OncePerRequestFilter {

    // Session
    private final JbstSessionRegistry sessionRegistry;
    // Extension
    private final JbstExtensionService extensionService;
    // Services
    private final JbstTokensService tokensService;
    // Tokens
    private final JbstTokensProvider tokensProvider;
    // Handlers
    private final JbstAccessDeniedHandler accessDeniedHandler;
    // Utils
    private final JbstSecurityUtils securityUtils;
    // Properties
    private final JbstProperties jbstProperties;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res, @NotNull FilterChain chain) throws ServletException, IOException {
        try {
            this.print(req);

            var cookieAccessToken = this.tokensProvider.readRequestAccessToken(req);
            var cookieRefreshToken = this.tokensProvider.readRequestRefreshToken(req);
            var user = this.tokensService.getJwtUserByAccessTokenOrThrow(cookieAccessToken, cookieRefreshToken);

            var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            this.sessionRegistry.register(new JbstSession(user.username(), cookieAccessToken.getJwtAccessToken(), cookieRefreshToken.getJwtRefreshToken()));

            this.extensionService.doFilter(req);

            chain.doFilter(req, res);
        } catch (
                JbstExceptions.AccessTokenNotFound |
                JbstExceptions.AccessTokenExpired ex
        ) {
            // distinguish authenticated vs. anonymous/permitAll endpoints
            chain.doFilter(req, res);
        } catch (
                JbstExceptions.RefreshTokenNotFound |
                JbstExceptions.AccessTokenInvalid |
                JbstExceptions.RefreshTokenInvalid |
                JbstExceptions.AccessTokenDbNotFound |
                JbstExceptions.ExtensionTokenUnauthorized ex
        ) {
            LOGGER.debug("JWT unauthorized request → clear cookies. Message: {}", ex.getMessage());
            this.tokensProvider.clearTokens(res);
            res.sendError(HttpStatus.UNAUTHORIZED.value());
        } catch (JbstExceptions.ExtensionTokenAccessDenied ex) {
            LOGGER.debug("JWT forbidden request → clear cookies. Message: {}", ex.getMessage());
            this.tokensProvider.clearTokens(res);
            this.accessDeniedHandler.handle(req, res, new AccessDeniedException(ex.getMessage()));
        }
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    public void print(HttpServletRequest req) {
        if (this.jbstProperties.getSecurity().getLogging().isAdvancedRequestLoggingEnabled()) {
            LOGGER.info(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
            LOGGER.info("User: {}", this.securityUtils.getAuthenticatedUsernameOrUnexpected());
            LOGGER.info("Method: @{} → {}", req.getMethod(), req.getServletPath());
            LOGGER.info(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
        }
    }
}
