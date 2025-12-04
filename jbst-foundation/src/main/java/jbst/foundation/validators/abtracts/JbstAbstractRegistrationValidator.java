package jbst.foundation.validators.abtracts;

import jbst.foundation.domain.dto.requests.JbstRequestUserRegistration0;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistration1;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistrationMagicLink;
import jbst.foundation.domain.events.JbstEventRegistration0Failure;
import jbst.foundation.domain.events.JbstEventRegistration1Failure;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.incidents.domain.registration.IncidentRegistration0Failure;
import jbst.foundation.incidents.domain.registration.IncidentRegistration1Failure;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.repositories.JbstInvitationsRepository;
import jbst.foundation.repositories.JbstUsersRepository;
import jbst.foundation.validators.JbstRegistrationValidator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.strings.JbstMessages.entityAlreadyUsed;
import static jbst.foundation.domain.strings.JbstMessages.entityNotFound;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class JbstAbstractRegistrationValidator implements JbstRegistrationValidator {

    // Publishers
    protected final JbstEventsPublisher eventsPublisher;
    protected final JbstIncidentsPublisher incidentsPublisher;
    // Repositories
    protected final JbstInvitationsRepository invitationsRepository;
    protected final JbstUsersRepository usersRepository;

    @Override
    public void validateRegistrationRequestMagicLink(JbstRequestUserRegistrationMagicLink request) {
        // no required actions
    }

    @Override
    public void validateRegistrationRequest0(JbstRequestUserRegistration0 request) throws JbstExceptions.Registration {
        request.assertPasswordsOrThrow();
        var existsByUsername = this.usersRepository.existsByUsername(request.username());
        if (existsByUsername) {
            var message = entityAlreadyUsed("Username", request.username().value());
            this.eventsPublisher.publishRegistration0Failure(
                    new JbstEventRegistration0Failure(
                            request.email(),
                            request.username(),
                            message
                    )
            );
            this.incidentsPublisher.publishRegistration0Failure(
                    new IncidentRegistration0Failure(
                            request.email(),
                            request.username(),
                            message
                    )
            );
            throw new JbstExceptions.Registration(message);
        }
        var existsByEmail = this.usersRepository.existsByEmail(request.email());
        if (existsByEmail) {
            var message = entityAlreadyUsed("Email", request.email().value());
            this.eventsPublisher.publishRegistration0Failure(
                    new JbstEventRegistration0Failure(
                            request.email(),
                            request.username(),
                            message
                    )
            );
            this.incidentsPublisher.publishRegistration0Failure(
                    new IncidentRegistration0Failure(
                            request.email(),
                            request.username(),
                            message
                    )
            );
            throw new JbstExceptions.Registration(message);
        }
    }

    @Override
    public void validateRegistrationRequest1(JbstRequestUserRegistration1 request) throws JbstExceptions.Registration {
        request.assertPasswordsOrThrow();
        var user = this.usersRepository.findByUsernameAsJwtUserOrNull(request.username());
        if (nonNull(user)) {
            var message = entityAlreadyUsed("Username", request.username().value());
            this.eventsPublisher.publishRegistration1Failure(
                    JbstEventRegistration1Failure.of(
                            request.username(),
                            request.code(),
                            message
                    )
            );
            this.incidentsPublisher.publishRegistration1Failure(
                    IncidentRegistration1Failure.of(
                            request.username(),
                            request.code(),
                            message
                    )
            );
            throw new JbstExceptions.Registration(message);
        }

        var invitation = this.invitationsRepository.findByCodeAsAny(request.code());
        if (nonNull(invitation)) {
            if (nonNull(invitation.invited())) {
                var message = entityAlreadyUsed("Code", invitation.code());
                this.eventsPublisher.publishRegistration1Failure(
                        new JbstEventRegistration1Failure(
                                request.username(),
                                request.code(),
                                invitation.owner(),
                                message
                        )
                );
                this.incidentsPublisher.publishRegistration1Failure(
                        new IncidentRegistration1Failure(
                                request.username(),
                                request.code(),
                                invitation.owner(),
                                message
                        )
                );
                throw new JbstExceptions.Registration(message);
            }
        } else {
            var exception = entityNotFound("Code", request.code());
            this.eventsPublisher.publishRegistration1Failure(
                    JbstEventRegistration1Failure.of(
                            request.username(),
                            request.code(),
                            exception
                    )
            );
            this.incidentsPublisher.publishRegistration1Failure(
                    IncidentRegistration1Failure.of(
                            request.username(),
                            request.code(),
                            exception
                    )
            );
            throw new JbstExceptions.Registration(exception);
        }
    }
}
