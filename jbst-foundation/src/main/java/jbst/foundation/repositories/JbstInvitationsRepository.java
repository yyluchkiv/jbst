package jbst.foundation.repositories;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstInvitation;
import jbst.foundation.domain.dto.requests.JbstRequestNewInvitationParams;
import jbst.foundation.domain.dto.responses.JbstResponseInvitation;
import jbst.foundation.domain.ids.JbstInvitationId;
import jbst.foundation.domain.tuples.TuplePresence;

import java.util.List;

public interface JbstInvitationsRepository {
    TuplePresence<JbstInvitation> isPresent(JbstInvitationId invitationId);
    List<JbstResponseInvitation> findResponseCodesByOwner(Username owner);
    JbstInvitation findByCodeAsAny(String value);
    List<JbstResponseInvitation> findUnused();
    long countByOwner(Username username);
    void delete(JbstInvitationId invitationId);
    JbstInvitationId saveAs(JbstInvitation invitation);
    JbstInvitationId saveAs(Username owner, JbstRequestNewInvitationParams request);
}
