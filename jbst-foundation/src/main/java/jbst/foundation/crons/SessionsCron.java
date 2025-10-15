package jbst.foundation.crons;

import jbst.foundation.domain.crons.AbstractBaseCron;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import jbst.foundation.services.BaseUsersSessionsService;
import jbst.foundation.sessions.AbstractJbstSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SessionsCron extends AbstractBaseCron {

    // Sessions
    private final AbstractJbstSessionRegistry sessionRegistry;
    // Services
    private final BaseUsersSessionsService baseUsersSessionsService;
    // Incidents
    private final IncidentPublisher incidentPublisher;
    // Properties
    private final JbstProperties jbstProperties;

    @Override
    public void processException(Exception ex) {
        this.incidentPublisher.publishThrowable(ex);
    }

    @Scheduled(
            cron = "${jbst.security-jwt-configs.session-configs.clean-sessions-by-expired-refresh-tokens-cron.expression}",
            zone = "${jbst.security-jwt-configs.session-configs.clean-sessions-by-expired-refresh-tokens-cron.zone-id}"
    )
    public void cleanByExpiredRefreshTokens() {
        this.executeCron(
                this.jbstProperties.getSecurityJwtConfigs().getSessionConfigs().getCleanSessionsByExpiredRefreshTokensCron().isEnabled(),
                () -> {
                    var usernames = this.sessionRegistry.getActiveSessionsUsernames();
                    LOGGER.info("Sessions cleanup by expired JWT refresh tokens executed. Active sessions usernames count: {}", usernames.size());
                    this.sessionRegistry.cleanByExpiredRefreshTokens(usernames);
                }
        );
    }

    @Scheduled(
            cron = "${jbst.security-jwt-configs.session-configs.enable-sessions-metadata-renew-cron.expression}",
            zone = "${jbst.security-jwt-configs.session-configs.enable-sessions-metadata-renew-cron.zone-id}"
    )
    public void enableSessionsMetadataRenew() {
        this.executeCron(
                this.jbstProperties.getSecurityJwtConfigs().getSessionConfigs().getEnableSessionsMetadataRenewCron().isEnabled(),
                this.baseUsersSessionsService::enableUserRequestMetadataRenewCron
        );
    }
}
