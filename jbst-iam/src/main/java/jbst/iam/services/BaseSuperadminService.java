package jbst.iam.services;

import jbst.iam.domain.dto.responses.ResponseInvitation;
import jbst.iam.domain.dto.responses.ResponseSuperadminSessionsTable;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.jwt.RequestAccessToken;
import org.springframework.scheduling.annotation.Async;
import jbst.foundation.domain.system.reset_server.ResetServerStatus;

import java.util.List;

public interface BaseSuperadminService {
    // =================================================================================================================
    // Server
    // =================================================================================================================
    ResetServerStatus getResetServerStatus();
    @Async
    void resetServerBy(JwtUser user);

    // =================================================================================================================
    // Invitations
    // =================================================================================================================

    List<ResponseInvitation> findUnused();

    // =================================================================================================================
    // Users Sessions
    // =================================================================================================================

    ResponseSuperadminSessionsTable getSessions(RequestAccessToken requestAccessToken);
}
