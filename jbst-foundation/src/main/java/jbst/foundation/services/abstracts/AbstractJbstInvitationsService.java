package jbst.foundation.services.abstracts;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.RequestNewInvitationParams;
import jbst.foundation.domain.dto.responses.ResponseInvitation;
import jbst.foundation.domain.dto.responses.ResponseInvitations;
import jbst.foundation.domain.ids.InvitationId;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.repositories.JbstInvitationsRepository;
import jbst.foundation.services.JbstInvitationsService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractJbstInvitationsService implements JbstInvitationsService {

    // Repositories
    protected final JbstInvitationsRepository invitationsRepository;
    // Properties
    protected final JbstProperties jbstProperties;

    @Override
    public ResponseInvitations findByOwner(Username owner) {
        var invitations = this.invitationsRepository.findResponseCodesByOwner(owner);
        invitations.sort(ResponseInvitation.INVITATION);
        return new ResponseInvitations(
                this.jbstProperties.getSecurity().getAuthoritiesConfigs().getAvailableAuthorities(),
                invitations
        );
    }

    @Override
    public void save(Username owner, RequestNewInvitationParams request) {
        this.invitationsRepository.saveAs(owner, request);
    }

    @Override
    public void deleteById(InvitationId invitationId) {
        this.invitationsRepository.delete(invitationId);
    }
}
