package jbst.foundation.validators.abtracts;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.RequestNewInvitationParams;
import jbst.foundation.domain.ids.InvitationId;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.repositories.JbstInvitationsRepository;
import jbst.foundation.validators.JbstInvitationsValidator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;

import static jbst.foundation.domain.asserts.Asserts.assertTrueOrThrow;
import static jbst.foundation.domain.collections.JbstCollections.baseJoiningRaw;
import static jbst.foundation.domain.strings.JbstMessages.entityAccessDenied;
import static jbst.foundation.domain.strings.JbstMessages.entityNotFound;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractJbstInvitationsValidator implements JbstInvitationsValidator {

    // Repositories
    protected final JbstInvitationsRepository invitationsRepository;
    // Properties
    protected final JbstProperties jbstProperties;

    @Override
    public void validateCreateNewInvitation(RequestNewInvitationParams request) {
        var availableAuthorities = this.jbstProperties.getSecurity().getAuthorities().getAvailableAuthorities();
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
