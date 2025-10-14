package jbst.iam.services;

import jbst.iam.domain.dto.requests.RequestNewInvitationParams;
import jbst.iam.domain.dto.responses.ResponseInvitations;
import jbst.foundation.domain.ids.InvitationId;
import jbst.foundation.domain.base.Username;

public interface BaseInvitationsService {
    ResponseInvitations findByOwner(Username owner);
    void save(Username owner, RequestNewInvitationParams request);
    void deleteById(InvitationId invitationId);
}
