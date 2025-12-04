package jbst.foundation.validators;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.JbstRequestNewInvitationParams;
import jbst.foundation.domain.ids.JbstInvitationId;

public interface JbstInvitationsValidator {
    void validateCreateNewInvitation(JbstRequestNewInvitationParams request);
    void validateDeleteById(Username username, JbstInvitationId invitationId);
}
