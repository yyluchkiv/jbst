package jbst.foundation.services;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUser;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.responses.ResponseInvitation;
import jbst.foundation.domain.dto.responses.ResponseSuperadminSessionsTable;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.system.reset_server.ResetServerStatus;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface JbstSuperadminService {
    // =================================================================================================================
    // Server
    // =================================================================================================================
    ResetServerStatus getResetServerStatus();
    @Async
    void resetServerBy(JwtUser user);

    // =================================================================================================================
    // Invitations
    // =================================================================================================================
    List<ResponseInvitation> findInvitationsUnused();

    // =================================================================================================================
    // Users
    // =================================================================================================================
    List<JbstUser> findUsersExcept(Username username);
    void disableUser(Username username);

    // =================================================================================================================
    // Users Sessions
    // =================================================================================================================
    ResponseSuperadminSessionsTable getSessions(RequestAccessToken requestAccessToken);
}
