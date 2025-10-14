package jbst.iam.repositories;

import jbst.iam.domain.db.Invitation;
import jbst.iam.domain.dto.requests.RequestNewInvitationParams;
import jbst.iam.domain.dto.responses.ResponseInvitation;
import jbst.foundation.domain.ids.InvitationId;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.tuples.TuplePresence;

import java.util.List;

public interface InvitationsRepository {
    TuplePresence<Invitation> isPresent(InvitationId invitationId);
    List<ResponseInvitation> findResponseCodesByOwner(Username owner);
    Invitation findByCodeAsAny(String value);
    List<ResponseInvitation> findUnused();
    long countByOwner(Username username);
    void delete(InvitationId invitationId);
    InvitationId saveAs(Invitation invitation);
    InvitationId saveAs(Username owner, RequestNewInvitationParams request);
}
