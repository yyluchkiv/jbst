package jbst.foundation.services.abstracts;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUsers;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.responses.ResponseInvitation;
import jbst.foundation.domain.dto.responses.ResponseSuperadminSessionsTable;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.system.JbstSystemResetServerStatus;
import jbst.foundation.incidents.domain.system.IncidentSystemResetServerCompleted;
import jbst.foundation.incidents.domain.system.IncidentSystemResetServerStarted;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.repositories.JbstInvitationsRepository;
import jbst.foundation.repositories.JbstUsersRepository;
import jbst.foundation.repositories.JbstUsersSessionsRepository;
import jbst.foundation.services.JbstSuperadminService;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.tasks.JbstAbstractTaskOnResetServer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractJbstSuperadminService implements JbstSuperadminService {

    // Incidents
    protected final JbstIncidentsPublisher incidentsPublisher;
    // Sessions
    protected final JbstSessionRegistry sessionRegistry;
    // Repositories
    protected final JbstInvitationsRepository invitationsRepository;
    protected final JbstUsersRepository usersRepository;
    protected final JbstUsersSessionsRepository usersSessionsRepository;
    // Tasks
    protected final JbstAbstractTaskOnResetServer taskOnResetServer;

    // =================================================================================================================
    // Server
    // =================================================================================================================
    @Override
    public JbstSystemResetServerStatus getResetServerStatus() {
        return this.taskOnResetServer.getStatus();
    }

    @Override
    public void resetServerBy(JwtUser user) {
        this.incidentsPublisher.publishResetServerStarted(new IncidentSystemResetServerStarted(user.username()));
        this.taskOnResetServer.reset(user);
        this.incidentsPublisher.publishResetServerCompleted(new IncidentSystemResetServerCompleted(user.username()));
    }

    // =================================================================================================================
    // Invitations
    // =================================================================================================================
    @Override
    public List<ResponseInvitation> findInvitationsUnused() {
        return this.invitationsRepository.findUnused();
    }

    // =================================================================================================================
    // Users
    // =================================================================================================================
    @Override
    public JbstUsers findUsersExcept(Username username) {
        return this.usersRepository.findUsersExcept(username);
    }

    @Override
    public void disableUser(Username username) {
        this.usersRepository.disable(username);
    }

    // =================================================================================================================
    // Users Sessions
    // =================================================================================================================
    @Override
    public ResponseSuperadminSessionsTable getSessions(RequestAccessToken requestAccessToken) {
        var activeAccessTokens = this.sessionRegistry.getActiveSessionsAccessTokens();
        return this.usersSessionsRepository.getSessionsTable(activeAccessTokens, requestAccessToken);
    }
}
