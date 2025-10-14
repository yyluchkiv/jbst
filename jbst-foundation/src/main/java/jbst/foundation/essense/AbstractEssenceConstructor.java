package jbst.foundation.essense;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.repositories.InvitationsRepository;
import jbst.foundation.repositories.UsersRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static jbst.foundation.domain.asserts.Asserts.assertTrueOrThrow;
import static jbst.foundation.domain.enums.Status.COMPLETED;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.invalidAttribute;
import static jbst.foundation.utilities.spring.SpringAuthoritiesUtility.getSimpleGrantedAuthorities;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractEssenceConstructor implements EssenceConstructor {

    // Repositories
    protected final InvitationsRepository invitationsRepository;
    protected final UsersRepository usersRepository;
    // Properties
    protected final JbstProperties jbstProperties;

    @SuppressWarnings("LoggingSimilarMessage")
    public void addDefaultUsers() {
        var essenceConfigs = this.jbstProperties.getSecurityJwtConfigs().getEssenceConfigs();
        assertTrueOrThrow(
                essenceConfigs.getDefaultUsers().isEnabled(),
                invalidAttribute("essenceConfigs.defaultUsers.enabled == true")
        );
        if (this.usersRepository.count() == 0L) {
            LOGGER.info(JbstConstants.Logs.PREFIX + " Essence 'default-users' — adding users to database");
            var usersCount = this.saveDefaultUsers(essenceConfigs.getDefaultUsers().getUsers());
            LOGGER.info(JbstConstants.Logs.PREFIX + " Essence 'default-users' — saved users: {}", usersCount);
        }
        LOGGER.info(JbstConstants.Logs.PREFIX + " Essence 'default-users' — {}", COMPLETED.asANSI());
    }

    public void addDefaultUsersInvitations() {
        var securityJwtConfigs = this.jbstProperties.getSecurityJwtConfigs();
        var essenceConfigs = securityJwtConfigs.getEssenceConfigs();
        assertTrueOrThrow(
                essenceConfigs.getInvitations().isEnabled(),
                invalidAttribute("essenceConfigs.invitations.enabled == true")
        );
        var authorities = getSimpleGrantedAuthorities(securityJwtConfigs.getAuthoritiesConfigs().getAvailableAuthorities());
        essenceConfigs.getDefaultUsers().getUsers().forEach(defaultUser -> {
            var username = defaultUser.getUsername();
            if (this.invitationsRepository.countByOwner(username) == 0L) {
                LOGGER.info(JbstConstants.Logs.PREFIX + " Essence 'invitations — add invitations, username: {}", username);
                this.saveInvitations(defaultUser, authorities);
            }
        });
        LOGGER.info(JbstConstants.Logs.PREFIX + " Essence 'invitations' — {}", COMPLETED.asANSI());
    }
}
