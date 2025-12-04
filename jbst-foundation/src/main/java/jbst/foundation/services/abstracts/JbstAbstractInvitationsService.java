package jbst.foundation.services.abstracts;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.JbstRequestNewInvitationParams;
import jbst.foundation.domain.dto.responses.JbstResponseInvitation;
import jbst.foundation.domain.dto.responses.JbstResponseInvitations;
import jbst.foundation.domain.ids.JbstInvitationId;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.repositories.JbstInvitationsRepository;
import jbst.foundation.services.JbstInvitationsService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class JbstAbstractInvitationsService implements JbstInvitationsService {

    // Repositories
    protected final JbstInvitationsRepository invitationsRepository;
    // Properties
    protected final JbstProperties jbstProperties;

    @Override
    public JbstResponseInvitations findByOwner(Username owner) {
        var invitations = this.invitationsRepository.findResponseCodesByOwner(owner);
        invitations.sort(JbstResponseInvitation.INVITATION);
        return new JbstResponseInvitations(
                this.jbstProperties.getSecurity().getAuthorities().getAvailableAuthorities(),
                invitations
        );
    }

    @Override
    public void save(Username owner, JbstRequestNewInvitationParams request) {
        this.invitationsRepository.saveAs(owner, request);
    }

    @Override
    public void deleteById(JbstInvitationId invitationId) {
        this.invitationsRepository.delete(invitationId);
    }
}
