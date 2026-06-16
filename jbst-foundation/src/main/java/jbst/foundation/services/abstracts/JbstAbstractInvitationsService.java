package jbst.foundation.services.abstracts;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.JbstRequestNewInvitationParams;
import jbst.foundation.domain.dto.responses.JbstResponseInvitation;
import jbst.foundation.domain.dto.responses.JbstResponseInvitations;
import jbst.foundation.domain.ids.JbstInvitationId;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.repositories.JbstInvitationsRepository;
import jbst.foundation.services.JbstInvitationsService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static jbst.foundation.domain.asserts.JbstAsserts.assertTrueOrThrow;
import static jbst.foundation.domain.constants.JbstConstants.Logs.PREFIX;
import static jbst.foundation.domain.enums.JbstStatus.COMPLETED;
import static jbst.foundation.domain.spring.JbstSpringAuthorities.getSimpleGrantedAuthorities;
import static jbst.foundation.domain.strings.JbstMessages.invalidAttribute;

@SuppressWarnings("LoggingSimilarMessage")
@Slf4j
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class JbstAbstractInvitationsService implements JbstInvitationsService {

    // Repositories
    protected final JbstInvitationsRepository invitationsRepository;
    // Properties
    protected final JbstProperties jbstProperties;

    @Override
    public void initInvitations() {
        var security = this.jbstProperties.getSecurity();
        var essence = security.getEssence();
        assertTrueOrThrow(
                essence.getInvitationsOnInit().isEnabled(),
                invalidAttribute("essence-configs.invitations-on-init.enabled == true")
        );
        var authorities = getSimpleGrantedAuthorities(security.getAuthorities().getAvailableAuthorities());
        essence.getUsersOnInit().getUsers().forEach(userOnInit -> {
            var username = userOnInit.getUsername();
            if (this.invitationsRepository.countByOwner(username) == 0L) {
                LOGGER.info("{} essence 'invitations-on-init' — add invitations, username: {}", PREFIX, username);
                this.initInvitations(userOnInit, authorities);
            }
        });
        LOGGER.info("{} essence 'invitations-on-init' — {}", PREFIX, COMPLETED.asANSI());
    }

    @Override
    public JbstResponseInvitations findByOwner(Username owner) {
        var invitations = this.invitationsRepository.findResponseCodesByOwner(owner);
        invitations.sort(JbstResponseInvitation.INVITATION);
        return new JbstResponseInvitations(
                this.jbstProperties.getSecurity().getAuthorities().getAvailableAuthorities(),
                invitations
        );
    }

    @Override
    public void save(Username owner, JbstRequestNewInvitationParams request) {
        this.invitationsRepository.saveAs(owner, request);
    }

    @Override
    public void deleteById(JbstInvitationId invitationId) {
        this.invitationsRepository.delete(invitationId);
    }
}
