package jbst.foundation.handshakes;

import jbst.foundation.domain.exceptions.tokens.JbstCsrfTokenNotFoundException;
import jbst.foundation.tokens.facade.TokensProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CsrfInterceptorHandshake implements HandshakeInterceptor {

    // Tokens
    private final TokensProvider tokensProvider;

    @Override
    public boolean beforeHandshake(
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            @NotNull Map<String, Object> attributes
    ) {
        try {
            var httpRequest = ((ServletServerHttpRequest) request).getServletRequest();
            var csrfToken = this.tokensProvider.readCsrfToken(httpRequest);
            attributes.put(CsrfToken.class.getName(), csrfToken);
            return true;
        } catch (JbstCsrfTokenNotFoundException ex1) {
            return false;
        } catch (RuntimeException ex2) {
            LOGGER.error("Please check websocket handshake configuration. Exception: {}", ex2.getMessage(), ex2);
            return false;
        }
    }

    @Override
    public void afterHandshake(
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            Exception exception
    ) {
        // no actions
    }
}
