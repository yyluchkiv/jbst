package jbst.iam.repositories;

import jbst.foundation.domain.databases.JbstInvitation;
import jbst.iam.domain.dto.requests.RequestNewInvitationParams;
import jbst.iam.domain.dto.responses.ResponseInvitation;
import jbst.foundation.domain.ids.InvitationId;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.tuples.TuplePresence;

import java.util.List;

public interface InvitationsRepository {
    TuplePresence<JbstInvitation> isPresent(InvitationId invitationId);
    List<ResponseInvitation> findResponseCodesByOwner(Username owner);
    JbstInvitation findByCodeAsAny(String value);
    List<ResponseInvitation> findUnused();
    long countByOwner(Username username);
    void delete(InvitationId invitationId);
    InvitationId saveAs(JbstInvitation invitation);
    InvitationId saveAs(Username owner, RequestNewInvitationParams request);
}
