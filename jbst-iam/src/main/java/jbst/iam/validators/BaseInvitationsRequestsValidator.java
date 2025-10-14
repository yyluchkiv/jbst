package jbst.iam.validators;

import jbst.iam.domain.dto.requests.RequestNewInvitationParams;
import jbst.foundation.domain.ids.InvitationId;
import jbst.foundation.domain.base.Username;

public interface BaseInvitationsRequestsValidator {
    void validateCreateNewInvitation(RequestNewInvitationParams request);
    void validateDeleteById(Username username, InvitationId invitationId);
}
