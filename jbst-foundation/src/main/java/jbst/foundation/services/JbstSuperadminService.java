package jbst.foundation.services;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUsers;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.responses.ResponseInvitation;
import jbst.foundation.domain.dto.responses.ResponseSuperadminSessionsTable;
import jbst.foundation.domain.jwt.JbstJwtUser;
import jbst.foundation.domain.system.JbstSystemResetServerStatus;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface JbstSuperadminService {
    // =================================================================================================================
    // Server
    // =================================================================================================================
    JbstSystemResetServerStatus getResetServerStatus();
    @Async
    void resetServerBy(JbstJwtUser user);

    // =================================================================================================================
    // Invitations
    // =================================================================================================================
    List<ResponseInvitation> findInvitationsUnused();

    // =================================================================================================================
    // Users
    // =================================================================================================================
    JbstUsers findUsersExcept(Username username);
    void disableUser(Username username);

    // =================================================================================================================
    // Users Sessions
    // =================================================================================================================
    ResponseSuperadminSessionsTable getSessions(RequestAccessToken requestAccessToken);
}
