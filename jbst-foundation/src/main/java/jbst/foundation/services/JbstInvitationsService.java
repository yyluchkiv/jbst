package jbst.foundation.services;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.JbstRequestNewInvitationParams;
import jbst.foundation.domain.dto.responses.JbstResponseInvitations;
import jbst.foundation.domain.ids.JbstInvitationId;

public interface JbstInvitationsService {
    JbstResponseInvitations findByOwner(Username owner);
    void save(Username owner, JbstRequestNewInvitationParams request);
    void deleteById(JbstInvitationId invitationId);
}
