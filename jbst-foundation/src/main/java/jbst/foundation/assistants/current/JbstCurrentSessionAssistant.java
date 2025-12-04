package jbst.foundation.assistants.current;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.responses.ResponseUserSessionsTable;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.jwt.JbstJwtUser;
import jbst.foundation.domain.security.JbstCurrentClientUser;
import jbst.foundation.repositories.JbstUsersSessionsRepository;
import jbst.foundation.resources.hardware.JbstHardwareMonitoringStore;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.settings.JbstSettingsService;
import jbst.foundation.tokens.facade.JbstTokensProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Set;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstCurrentSessionAssistant implements CurrentSessionAssistant {
    // Settings
    protected final JbstSettingsService jbstSettingsService;
    // Sessions
    protected final JbstSessionRegistry sessionRegistry;
    // Repositories
    protected final JbstUsersSessionsRepository usersSessionsRepository;
    // Tokens
    protected final JbstTokensProvider tokensProvider;
    // Utilities
    protected final JbstSecurityUtils securityUtils;
    // Stores
    protected final JbstHardwareMonitoringStore jbstHardwareMonitoringStore;

    @Override
    public Username getCurrentUsername() {
        return Username.of(this.securityUtils.getAuthenticatedUsername());
    }

    @Override
    public JbstJwtUser getCurrentJwtUser() {
        return this.securityUtils.getAuthenticatedJwtUser();
    }

    @Override
    public JbstCurrentClientUser getCurrentClientUser() {
        var user = this.getCurrentJwtUser();

        var attributes = nonNull(user.attributes()) ? user.attributes() : new HashMap<String, Object>();

        if (this.jbstSettingsService.isHardwareMonitoringThresholdsEnabled()) {
            attributes.put("hardware", this.jbstHardwareMonitoringStore.getWidget());
        }

        return new JbstCurrentClientUser(
                user.username(),
                user.email(),
                user.name(),
                user.zoneId(),
                user.passwordChangeRequired(),
                user.emailDetails(),
                user.authorities(),
                attributes
        );
    }

    @Override
    public JbstUserSession getCurrentUserSession(HttpServletRequest httpServletRequest) throws JbstExceptions.AccessTokenNotFound {
        var cookie = this.tokensProvider.readRequestAccessToken(httpServletRequest);
        return this.usersSessionsRepository.isPresent(JbstJwtAccessToken.of(cookie.value())).value();
    }

    @Override
    public ResponseUserSessionsTable getCurrentUserDbSessionsTable(RequestAccessToken requestAccessToken) {
        var username = this.getCurrentUsername();
        this.sessionRegistry.cleanByExpiredRefreshTokens(Set.of(username));
        return this.sessionRegistry.getSessionsTable(username, requestAccessToken);
    }
}
