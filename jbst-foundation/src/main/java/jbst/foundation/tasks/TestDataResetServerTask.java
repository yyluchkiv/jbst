package jbst.foundation.tasks;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.system.reset_server.ResetServerStatus;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import jbst.foundation.websockets.JbstWebsocketsService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

import static jbst.foundation.domain.enums.Status.FAILURE;
import static jbst.foundation.utilities.concurrent.SleepUtility.sleepMilliseconds;

@Slf4j
@Getter
@Component
public class TestDataResetServerTask extends AbstractSuperAdminResetServerTask {

    // Wss
    private final JbstWebsocketsService websocketsService;

    private final ResetServerStatus status = new ResetServerStatus(6);

    @Autowired
    public TestDataResetServerTask(
            IncidentPublisher incidentPublisher,
            JbstWebsocketsService websocketsService
    ) {
        super(
                incidentPublisher
        );
        this.websocketsService = websocketsService;
    }

    @Override
    public void resetOnServer(JwtUser initiator) {
        var username = initiator.username();
        var usernames = Set.of(username);
        try {
            this.status.reset();

            this.computeAndSendResetServerProgress(usernames, "[Server] Test Data Stage #1");
            this.computeAndSendResetServerProgress(usernames, "[Server] Test Data Stage #2");
            this.computeAndSendResetServerProgress(usernames, "[Server] Test Data Stage #3");
            this.computeAndSendResetServerProgress(usernames, "[Server] Test Data Stage #4");
            this.computeAndSendResetServerProgress(usernames, "[Server] Test Data Stage #5");
            this.computeAndSendResetServerProgress(usernames, "[Server] Test Data Stage #6");

            this.status.complete(initiator.zoneId());
            this.websocketsService.sendResetServerStatus(usernames, this.status);
        } catch (RuntimeException ex) {
            // WARNING: any exceptions should NOT be expected behaviour, method required ASAP fix
            this.status.setFailureDescription(ex);
            this.websocketsService.sendResetServerStatus(usernames, this.status);
            LOGGER.info(JbstConstants.Logs.getUserProcess(initiator.username(), "Reset Server", FAILURE));
            this.incidentPublisher.publishThrowable(ex);
        }
    }

    private void computeAndSendResetServerProgress(Set<Username> usernames, String description) {
        this.status.nextStage(description);
        this.websocketsService.sendResetServerStatus(usernames, this.status);
        sleepMilliseconds(1000);
    }
}
