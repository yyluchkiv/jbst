package jbst.iam.repositories.postgres;

import jbst.iam.domain.db.Invitation;
import jbst.iam.domain.dto.requests.RequestNewInvitationParams;
import jbst.iam.domain.dto.responses.ResponseInvitation;
import jbst.iam.domain.identifiers.InvitationId;
import jbst.iam.repositories.InvitationsRepository;
import jbst.iam.domain.postgres.db.PostgresDbInvitation;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.tuples.TuplePresence;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static jbst.iam.domain.db.Invitation.INVITATION_CODES_UNUSED;
import static jbst.foundation.utilities.spring.SpringAuthoritiesUtility.getSimpleGrantedAuthorities;
import static jbst.foundation.domain.tuples.TuplePresence.present;
import static java.util.Objects.nonNull;

@SuppressWarnings("JpaQlInspection")
public interface PostgresInvitationsRepository extends JpaRepository<PostgresDbInvitation, UUID>, InvitationsRepository {
    // ================================================================================================================
    // Any
    // ================================================================================================================
    default TuplePresence<Invitation> isPresent(InvitationId invitationId) {
        return this.findById(invitationId.asUUID())
                .map(entity -> present(entity.invitation()))
                .orElseGet(TuplePresence::absent);
    }

    default List<ResponseInvitation> findResponseCodesByOwner(Username owner) {
        return this.findByOwner(owner).stream()
                .map(PostgresDbInvitation::responseInvitation)
                .collect(Collectors.toList());
    }

    default Invitation findByCodeAsAny(String code) {
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
            this.deleteById(invitationId.asUUID());
        }
    }

    default InvitationId saveAs(Invitation invitation) {
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
    @Modifying
    void deleteByInvitedIsNull();

    @Transactional
    @Modifying
    void deleteByInvitedIsNotNull();
}
