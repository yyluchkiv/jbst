package jbst.foundation.repositories.mongo;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstInvitation;
import jbst.foundation.domain.databases.mongo.JbstMongoInvitation;
import jbst.foundation.domain.dto.requests.JbstRequestNewInvitationParams;
import jbst.foundation.domain.dto.responses.JbstResponseInvitation;
import jbst.foundation.domain.ids.JbstInvitationId;
import jbst.foundation.domain.tuples.TuplePresence;
import jbst.foundation.repositories.JbstInvitationsRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.databases.JbstInvitation.INVITATION_CODES_UNUSED;
import static jbst.foundation.domain.tuples.TuplePresence.present;
import static jbst.foundation.domain.spring.JbstSpringAuthorities.getSimpleGrantedAuthorities;

public interface JbstMongoInvitationsRepository extends MongoRepository<JbstMongoInvitation, String>, JbstInvitationsRepository {
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
                .map(JbstMongoInvitation::responseInvitation)
                .toList();
    }

    default JbstInvitation findByCodeAsAny(String code) {
        var invitation = this.findByCode(code);
        return nonNull(invitation) ? invitation.invitation() : null;
    }

    default List<JbstResponseInvitation> findUnused() {
        return this.findByInvitedIsNull(INVITATION_CODES_UNUSED).stream()
                .map(JbstMongoInvitation::responseInvitation)
                .toList();
    }

    long countByOwner(Username username);

    default void delete(JbstInvitationId invitationId) {
        this.deleteById(invitationId.value());
    }

    default JbstInvitationId saveAs(JbstInvitation invitation) {
        var entity = this.save(new JbstMongoInvitation(invitation));
        return entity.invitationId();
    }

    default JbstInvitationId saveAs(Username owner, JbstRequestNewInvitationParams request) {
        var invitation = new JbstMongoInvitation(
                owner,
                getSimpleGrantedAuthorities(request.authorities())
        );
        var entity = this.save(invitation);
        return entity.invitationId();
    }

    // ================================================================================================================
    // Spring Data
    // ================================================================================================================
    List<JbstMongoInvitation> findByOwner(Username username);
    List<JbstMongoInvitation> findByInvitedIsNull();
    List<JbstMongoInvitation> findByInvitedIsNull(Sort sort);
    List<JbstMongoInvitation> findByInvitedIsNotNull();
    List<JbstMongoInvitation> findByInvitedIsNotNull(Sort sort);
    JbstMongoInvitation findByCode(String code);

    void deleteByInvitedIsNull();
    void deleteByInvitedIsNotNull();
}
