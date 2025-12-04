package jbst.foundation.repositories.postgres;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstInvitation;
import jbst.foundation.domain.databases.postgres.entities.JbstPostgresInvitation;
import jbst.foundation.domain.dto.requests.JbstRequestNewInvitationParams;
import jbst.foundation.domain.dto.responses.JbstResponseInvitation;
import jbst.foundation.domain.ids.JbstInvitationId;
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
public interface JbstPostgresInvitationsRepository extends JpaRepository<JbstPostgresInvitation, String>, JbstInvitationsRepository {
    // ================================================================================================================
    // Any
    // ================================================================================================================
    default TuplePresence<JbstInvitation> isPresent(JbstInvitationId invitationId) {
        return this.findById(invitationId.value())
                .map(entity -> present(entity.invitation()))
                .orElseGet(TuplePresence::absent);
    }

    default List<JbstResponseInvitation> findResponseCodesByOwner(Username owner) {
        return this.findByOwner(owner).stream()
                .map(JbstPostgresInvitation::responseInvitation)
                .collect(Collectors.toList());
    }

    default JbstInvitation findByCodeAsAny(String code) {
        var invitation = this.findByCode(code);
        return nonNull(invitation) ? invitation.invitation() : null;
    }

    default List<JbstResponseInvitation> findUnused() {
        return this.findByInvitedIsNull(INVITATION_CODES_UNUSED).stream()
                .map(JbstPostgresInvitation::responseInvitation)
                .collect(Collectors.toList());
    }

    long countByOwner(Username username);

    default void delete(JbstInvitationId invitationId) {
        var tuplePresence = this.isPresent(invitationId);
        if (tuplePresence.present()) {
            this.deleteById(invitationId.value());
        }
    }

    default JbstInvitationId saveAs(JbstInvitation invitation) {
        var entity = this.save(new JbstPostgresInvitation(invitation));
        return entity.invitationId();
    }

    default JbstInvitationId saveAs(Username owner, JbstRequestNewInvitationParams request) {
        var invitation = new JbstPostgresInvitation(
                owner,
                getSimpleGrantedAuthorities(request.authorities())
        );
        var entity = this.save(invitation);
        return entity.invitationId();
    }

    // ================================================================================================================
    // Spring Data
    // ================================================================================================================
    List<JbstPostgresInvitation> findByOwner(Username username);
    List<JbstPostgresInvitation> findByInvitedIsNull();
    List<JbstPostgresInvitation> findByInvitedIsNull(Sort sort);
    List<JbstPostgresInvitation> findByInvitedIsNotNull();
    List<JbstPostgresInvitation> findByInvitedIsNotNull(Sort sort);
    JbstPostgresInvitation findByCode(String value);

    @Transactional
    void deleteByInvitedIsNull();

    @Transactional
    void deleteByInvitedIsNotNull();
}
