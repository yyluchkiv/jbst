package jbst.foundation.validators.abtracts;

import jbst.foundation.domain.dto.requests.RequestNewInvitationParams;
import jbst.foundation.domain.ids.InvitationId;
import jbst.foundation.repositories.InvitationsRepository;
import jbst.foundation.validators.BaseInvitationsRequestsValidator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.properties.JbstProperties;

import static jbst.foundation.domain.asserts.Asserts.assertTrueOrThrow;
import static jbst.foundation.utilities.collections.CollectionUtility.baseJoiningRaw;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.entityAccessDenied;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.entityNotFound;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBaseInvitationsRequestsValidator implements BaseInvitationsRequestsValidator {

    // Repositories
    protected final InvitationsRepository invitationsRepository;
    // Properties
    protected final JbstProperties jbstProperties;

    @Override
    public void validateCreateNewInvitation(RequestNewInvitationParams request) {
        var availableAuthorities = this.jbstProperties.getSecurityJwtConfigs().getAuthoritiesConfigs().getAvailableAuthorities();
        assertTrueOrThrow(
                availableAuthorities.containsAll(request.authorities()),
                "Authorities must contains: [%s]".formatted(baseJoiningRaw(availableAuthorities))
        );
    }

    @Override
    public void validateDeleteById(Username username, InvitationId invitationId) {
        var tuplePresence = this.invitationsRepository.isPresent(invitationId);
        if (!tuplePresence.present()) {
            throw new IllegalArgumentException(entityNotFound("Invitation", invitationId.value()));
        }
        if (!username.equals(tuplePresence.value().owner())) {
            throw new AccessDeniedException(entityAccessDenied("Invitation", invitationId.value()));
        }
    }
}
