package jbst.foundation.validators;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.RequestNewInvitationParams;
import jbst.foundation.domain.ids.InvitationId;

public interface JbstInvitationsValidator {
    void validateCreateNewInvitation(RequestNewInvitationParams request);
    void validateDeleteById(Username username, InvitationId invitationId);
}
