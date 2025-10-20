package jbst.foundation.services;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.RequestNewInvitationParams;
import jbst.foundation.domain.dto.responses.ResponseInvitations;
import jbst.foundation.domain.ids.InvitationId;

public interface JbstInvitationsService {
    ResponseInvitations findByOwner(Username owner);
    void save(Username owner, RequestNewInvitationParams request);
    void deleteById(InvitationId invitationId);
}
