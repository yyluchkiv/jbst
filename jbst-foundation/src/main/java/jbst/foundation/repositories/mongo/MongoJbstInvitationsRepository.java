package jbst.foundation.repositories.mongo;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstInvitation;
import jbst.foundation.domain.databases.mongo.MongoDbInvitation;
import jbst.foundation.domain.dto.requests.RequestNewInvitationParams;
import jbst.foundation.domain.dto.responses.ResponseInvitation;
import jbst.foundation.domain.ids.JbstInvitationId;
import jbst.foundation.domain.tuples.TuplePresence;
import jbst.foundation.repositories.JbstInvitationsRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.databases.JbstInvitation.INVITATION_CODES_UNUSED;
import static jbst.foundation.domain.tuples.TuplePresence.present;
import static jbst.foundation.domain.spring.JbstSpringAuthorities.getSimpleGrantedAuthorities;

public interface MongoJbstInvitationsRepository extends MongoRepository<MongoDbInvitation, String>, JbstInvitationsRepository {
    // ================================================================================================================
    // Any
    // ================================================================================================================
    default TuplePresence<JbstInvitation> isPresent(JbstInvitationId invitationId) {
        return this.findById(invitationId.value())
                .map(entity -> present(entity.invitation()))
                .orElseGet(TuplePresence::absent);
    }

    default List<ResponseInvitation> findResponseCodesByOwner(Username owner) {
        return this.findByOwner(owner).stream()
                .map(MongoDbInvitation::responseInvitation)
                .collect(Collectors.toList());
    }

    default JbstInvitation findByCodeAsAny(String code) {
        var invitation = this.findByCode(code);
        return nonNull(invitation) ? invitation.invitation() : null;
    }

    default List<ResponseInvitation> findUnused() {
        return this.findByInvitedIsNull(INVITATION_CODES_UNUSED).stream()
                .map(MongoDbInvitation::responseInvitation)
                .collect(Collectors.toList());
    }

    long countByOwner(Username username);

    default void delete(JbstInvitationId invitationId) {
        this.deleteById(invitationId.value());
    }

    default JbstInvitationId saveAs(JbstInvitation invitation) {
        var entity = this.save(new MongoDbInvitation(invitation));
        return entity.invitationId();
    }

    default JbstInvitationId saveAs(Username owner, RequestNewInvitationParams request) {
        var invitation = new MongoDbInvitation(
                owner,
                getSimpleGrantedAuthorities(request.authorities())
        );
        var entity = this.save(invitation);
        return entity.invitationId();
    }

    // ================================================================================================================
    // Spring Data
    // ================================================================================================================
    List<MongoDbInvitation> findByOwner(Username username);
    List<MongoDbInvitation> findByInvitedIsNull();
    List<MongoDbInvitation> findByInvitedIsNull(Sort sort);
    List<MongoDbInvitation> findByInvitedIsNotNull();
    List<MongoDbInvitation> findByInvitedIsNotNull(Sort sort);
    MongoDbInvitation findByCode(String code);

    void deleteByInvitedIsNull();
    void deleteByInvitedIsNotNull();
}
