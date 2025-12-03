package jbst.foundation.handshakes;

import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.services.base.JbstTokensService;
import jbst.foundation.tokens.facade.JbstTokensProvider;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstSecurityHandshakeHandler extends DefaultHandshakeHandler {

    // Services
    private final JbstTokensService tokensService;
    // Tokens
    private final JbstTokensProvider tokensProvider;

    @Override
    protected Principal determineUser(
            @NotNull ServerHttpRequest serverHttpRequest,
            @NotNull WebSocketHandler wsHandler,
            @NotNull Map<String, Object> attributes
    ) {
        try {
            var request = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();
            var cookieAccessToken = this.tokensProvider.readRequestAccessTokenOnWebsocketHandshake(request);
            var cookieRefreshToken = this.tokensProvider.readRequestRefreshTokenOnWebsocketHandshake(request);
            var user = this.tokensService.getJwtUserByAccessTokenOrThrow(cookieAccessToken, cookieRefreshToken);
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        } catch (JbstExceptions.AccessTokenNotFound | JbstExceptions.RefreshTokenNotFound |
                 JbstExceptions.AccessTokenInvalid |
                 JbstExceptions.RefreshTokenInvalid | JbstExceptions.AccessTokenExpired |
                 JbstExceptions.AccessTokenDbNotFound ex) {
            throw new IllegalArgumentException("WebSocket user not determined. Exception: " + ex.getMessage());
        }
    }
}
