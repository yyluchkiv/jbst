package jbst.foundation.repositories.postgres;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstInvitation;
import jbst.foundation.domain.databases.postgres.entities.PostgresDbInvitation;
import jbst.foundation.domain.dto.requests.RequestNewInvitationParams;
import jbst.foundation.domain.dto.responses.ResponseInvitation;
import jbst.foundation.domain.ids.InvitationId;
import jbst.foundation.domain.tuples.TuplePresence;
import jbst.foundation.repositories.JbstInvitationsRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.databases.JbstInvitation.INVITATION_CODES_UNUSED;
import static jbst.foundation.domain.tuples.TuplePresence.present;
import static jbst.foundation.domain.spring.JbstSpringAuthorities.getSimpleGrantedAuthorities;

@SuppressWarnings("JpaQlInspection")
public interface PostgresJbstInvitationsRepository extends JpaRepository<PostgresDbInvitation, String>, JbstInvitationsRepository {
    // ================================================================================================================
    // Any
    // ================================================================================================================
    default TuplePresence<JbstInvitation> isPresent(InvitationId invitationId) {
        return this.findById(invitationId.value())
                .map(entity -> present(entity.invitation()))
                .orElseGet(TuplePresence::absent);
    }

    default List<ResponseInvitation> findResponseCodesByOwner(Username owner) {
        return this.findByOwner(owner).stream()
                .map(PostgresDbInvitation::responseInvitation)
                .collect(Collectors.toList());
    }

    default JbstInvitation findByCodeAsAny(String code) {
        var invitation = this.findByCode(code);
        return nonNull(invitation) ? invitation.invitation() : null;
    }

    default List<ResponseInvitation> findUnused() {
        return this.findByInvitedIsNull(INVITATION_CODES_UNUSED).stream()
                .map(PostgresDbInvitation::responseInvitation)
                .collect(Collectors.toList());
    }

    long countByOwner(Username username);

    default void delete(InvitationId invitationId) {
        var tuplePresence = this.isPresent(invitationId);
        if (tuplePresence.present()) {
            this.deleteById(invitationId.value());
        }
    }

    default InvitationId saveAs(JbstInvitation invitation) {
        var entity = this.save(new PostgresDbInvitation(invitation));
        return entity.invitationId();
    }

    default InvitationId saveAs(Username owner, RequestNewInvitationParams request) {
        var invitation = new PostgresDbInvitation(
                owner,
                getSimpleGrantedAuthorities(request.authorities())
        );
        var entity = this.save(invitation);
        return entity.invitationId();
    }

    // ================================================================================================================
    // Spring Data
    // ================================================================================================================
    List<PostgresDbInvitation> findByOwner(Username username);
    List<PostgresDbInvitation> findByInvitedIsNull();
    List<PostgresDbInvitation> findByInvitedIsNull(Sort sort);
    List<PostgresDbInvitation> findByInvitedIsNotNull();
    List<PostgresDbInvitation> findByInvitedIsNotNull(Sort sort);
    PostgresDbInvitation findByCode(String value);

    @Transactional
    void deleteByInvitedIsNull();

    @Transactional
    void deleteByInvitedIsNotNull();
}
