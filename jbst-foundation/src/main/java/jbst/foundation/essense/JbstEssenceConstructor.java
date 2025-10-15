package jbst.foundation.essense;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.DefaultUser;
import jbst.foundation.repositories.JbstInvitationsRepository;
import jbst.foundation.repositories.JbstUsersRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;

import static jbst.foundation.domain.asserts.Asserts.assertTrueOrThrow;
import static jbst.foundation.domain.enums.Status.COMPLETED;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.invalidAttribute;
import static jbst.foundation.utilities.spring.SpringAuthoritiesUtility.getSimpleGrantedAuthorities;

// TODO [YYL] merge Essense Constructor <-> Settings Service
@Slf4j
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class JbstEssenceConstructor {

    // Repositories
    protected final JbstInvitationsRepository invitationsRepository;
    protected final JbstUsersRepository usersRepository;
    // Properties
    protected final JbstProperties jbstProperties;

    abstract public long saveDefaultUsers(List<DefaultUser> defaultUsers);
    abstract public void saveInvitations(DefaultUser defaultUser, Set<SimpleGrantedAuthority> authorities);

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
