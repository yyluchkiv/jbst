package jbst.iam.crons;

import jbst.foundation.domain.crons.AbstractBaseCron;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import jbst.iam.repositories.UsersTokensRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UsersTokensCron extends AbstractBaseCron {

    // Repository
    private final UsersTokensRepository usersTokensRepository;
    // Incidents
    private final IncidentPublisher incidentPublisher;

    @Override
    public void processException(Exception ex) {
        this.incidentPublisher.publishThrowable(ex);
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
