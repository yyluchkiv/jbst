package jbst.foundation.filters.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.exceptions.tokens.*;
import jbst.foundation.domain.sessions.Session;
import jbst.foundation.filters.jwt_extension.JwtTokensFilterExtension;
import jbst.foundation.handlers.JwtAccessDeniedExceptionHandler;
import jbst.foundation.services.base.JbstTokensService;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.tokens.facade.TokensProvider;
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
public class JwtTokensFilter extends OncePerRequestFilter {

    // Session
    private final JbstSessionRegistry sessionRegistry;
    // Services
    private final JbstTokensService tokensService;
    // Tokens
    private final TokensProvider tokensProvider;
    // Extension
    private final JwtTokensFilterExtension jwtTokensFilterExtension;
    // Handlers
    private final JwtAccessDeniedExceptionHandler jwtAccessDeniedExceptionHandler;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res, @NotNull FilterChain chain) throws ServletException, IOException {
        try {
            var cookieAccessToken = this.tokensProvider.readRequestAccessToken(req);
            var cookieRefreshToken = this.tokensProvider.readRequestRefreshToken(req);
            var user = this.tokensService.getJwtUserByAccessTokenOrThrow(cookieAccessToken, cookieRefreshToken);

            var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            this.sessionRegistry.register(new Session(user.username(), cookieAccessToken.getJwtAccessToken(), cookieRefreshToken.getJwtRefreshToken()));

            this.jwtTokensFilterExtension.doFilter(req);

            chain.doFilter(req, res);
        } catch (
                JbstAccessTokenNotFoundException |
                JbstAccessTokenExpiredException ex
        ) {
            // distinguish authenticated vs. anonymous/permitAll endpoints
            chain.doFilter(req, res);
        } catch (
                JbstRefreshTokenNotFoundException |
                JbstAccessTokenInvalidException |
                JbstRefreshTokenInvalidException |
                JbstAccessTokenDbNotFoundException |
                JbstTokenExtensionUnauthorizedException ex
        ) {
            LOGGER.debug("JWT unauthorized request → clear cookies. Message: {}", ex.getMessage());
            this.tokensProvider.clearTokens(res);
            res.sendError(HttpStatus.UNAUTHORIZED.value());
        } catch (JbstTokenExtensionAccessDeniedException ex) {
            LOGGER.debug("JWT forbidden request → clear cookies. Message: {}", ex.getMessage());
            this.tokensProvider.clearTokens(res);
            this.jwtAccessDeniedExceptionHandler.handle(req, res, new AccessDeniedException(ex.getMessage()));
        }
    }
}
