package jbst.foundation.crons;

import jbst.foundation.domain.crons.AbstractBaseCron;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.services.JbstUsersSessionsService;
import jbst.foundation.sessions.JbstSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstSessionsCron extends AbstractBaseCron {

    // Sessions
    private final JbstSessionRegistry sessionRegistry;
    // Services
    private final JbstUsersSessionsService usersSessionsService;
    // Incidents
    private final JbstIncidentsPublisher incidentsPublisher;
    // Properties
    private final JbstProperties jbstProperties;

    @Override
    public void processException(Exception ex) {
        this.incidentsPublisher.publishThrowable(ex);
    }

    @Scheduled(
            cron = "${jbst.security.sessions.clean-sessions-by-expired-refresh-tokens-cron.expression}",
            zone = "${jbst.security.sessions.clean-sessions-by-expired-refresh-tokens-cron.zone-id}"
    )
    public void cleanByExpiredRefreshTokens() {
        this.executeCron(
                this.jbstProperties.getSecurity().getSessions().getCleanSessionsByExpiredRefreshTokensCron().isEnabled(),
                () -> {
                    var usernames = this.sessionRegistry.getActiveSessionsUsernames();
                    LOGGER.info("Sessions cleanup by expired JWT refresh tokens executed. Active sessions usernames count: {}", usernames.size());
                    this.sessionRegistry.cleanByExpiredRefreshTokens(usernames);
                }
        );
    }

    @Scheduled(
            cron = "${jbst.security.sessions.enable-sessions-metadata-renew-cron.expression}",
            zone = "${jbst.security.sessions.enable-sessions-metadata-renew-cron.zone-id}"
    )
    public void enableSessionsMetadataRenew() {
        this.executeCron(
                this.jbstProperties.getSecurity().getSessions().getEnableSessionsMetadataRenewCron().isEnabled(),
                this.usersSessionsService::enableUserRequestMetadataRenewCron
        );
    }
}
