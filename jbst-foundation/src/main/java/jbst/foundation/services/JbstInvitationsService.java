package jbst.foundation.services;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.JbstRequestNewInvitationParams;
import jbst.foundation.domain.dto.responses.JbstResponseInvitations;
import jbst.foundation.domain.ids.JbstInvitationId;
import jbst.foundation.domain.properties.base.JbstPropertyUserOnInit;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

public interface JbstInvitationsService {
    void initInvitations();
    void initInvitations(JbstPropertyUserOnInit userOnInit, Set<SimpleGrantedAuthority> authorities);
    JbstResponseInvitations findByOwner(Username owner);
    void save(Username owner, JbstRequestNewInvitationParams request);
    void deleteById(JbstInvitationId invitationId);
}
