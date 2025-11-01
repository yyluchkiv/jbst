package jbst.foundation.crons;

import jbst.foundation.domain.crons.AbstractBaseCron;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.repositories.JbstUsersTokensRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstUsersTokensCron extends AbstractBaseCron {

    // Repository
    private final JbstUsersTokensRepository usersTokensRepository;
    // Incidents
    private final JbstIncidentsPublisher incidentsPublisher;

    @Override
    public void processException(Exception ex) {
        this.incidentsPublisher.publishThrowable(ex);
    }

    @Scheduled(cron = "0 1 * * * *")
    public void cleanup() {
        this.alwaysExecuteCron(
                () -> {
                    this.usersTokensRepository.cleanupExpired();
                    this.usersTokensRepository.cleanupUsed();
                }
        );
    }
}
