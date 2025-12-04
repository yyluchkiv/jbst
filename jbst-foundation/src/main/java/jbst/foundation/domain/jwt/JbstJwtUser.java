package jbst.foundation.domain.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserEmailDetails;
import jbst.foundation.domain.dto.requests.JbstRequestUserToken;
import jbst.foundation.domain.enums.JbstUserCreationOption;
import jbst.foundation.domain.enums.JbstUserTokenType;
import jbst.foundation.domain.ids.JbstUserId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.ZoneId;
import java.util.*;

import static jbst.foundation.domain.base.AbstractAuthority.*;
import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static jbst.foundation.domain.random.JbstRandom.*;
import static jbst.foundation.domain.spring.JbstSpringAuthorities.getSimpleGrantedAuthorities;

public record JbstJwtUser(
        JbstUserId id,
        JbstUserCreationOption creationOption,
        Username username,
        Password password,
        boolean enabled,
        ZoneId zoneId,
        Set<SimpleGrantedAuthority> authorities,
        Email email,
        String name,
        boolean passwordChangeRequired,
        JbstUserEmailDetails emailDetails,
        Map<String, Object> attributes
) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password.value();
    }

    @Override
    public String getUsername() {
        return this.username.value();
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public static JbstJwtUser hardcoded(JbstUserCreationOption userCreationOption) {
        return new JbstJwtUser(
                JbstUserId.hardcoded(),
                userCreationOption,
                Username.hardcoded(),
                Password.hardcoded(),
                true,
                UKRAINE,
                new HashSet<>(),
                Email.hardcoded(),
                "",
                false,
                JbstUserEmailDetails.unnecessary(),
                new HashMap<>()
        );
    }

    public static JbstJwtUser hardcoded() {
        return hardcoded(
                getSimpleGrantedAuthorities("user")
        );
    }

    public static JbstJwtUser hardcoded(Set<SimpleGrantedAuthority> authorities) {
        return new JbstJwtUser(
                JbstUserId.hardcoded(),
                JbstUserCreationOption.hardcoded(),
                Username.hardcoded(),
                Password.hardcoded(),
                true,
                UKRAINE,
                authorities,
                Email.hardcoded(),
                "",
                false,
                JbstUserEmailDetails.confirmed(),
                new HashMap<>()
        );
    }

    public static JbstJwtUser hardcoded(
            Email email,
            JbstUserEmailDetails emailDetails
    ) {
        return new JbstJwtUser(
                JbstUserId.hardcoded(),
                JbstUserCreationOption.hardcoded(),
                Username.hardcoded(),
                Password.hardcoded(),
                true,
                UKRAINE,
                new HashSet<>(),
                email,
                "",
                false,
                emailDetails,
                new HashMap<>()
        );
    }

    public static JbstJwtUser hardcoded(Map<String, Object> attributes) {
        var user = JbstJwtUser.hardcoded();
        user.attributes().putAll(attributes);
        return user;
    }

    public static JbstJwtUser random() {
        return new JbstJwtUser(
                JbstUserId.random(),
                JbstUserCreationOption.random(),
                Username.random(),
                Password.random(),
                true,
                randomZoneId(),
                Set.of(
                        new SimpleGrantedAuthority(randomElement(List.of(SUPERADMIN, INVITATIONS_READ, INVITATIONS_WRITE)))
                ),
                Email.random(),
                randomString(),
                randomBoolean(),
                JbstUserEmailDetails.random(),
                new HashMap<>(
                        Map.of(
                            randomString(), randomString(),
                            randomString(), randomInteger()
                        )
                )
        );
    }

    public static JbstJwtUser randomSuperadmin() {
        return new JbstJwtUser(
                JbstUserId.random(),
                JbstUserCreationOption.random(),
                Username.random(),
                Password.random(),
                true,
                randomZoneId(),
                getSimpleGrantedAuthorities(SUPERADMIN),
                Email.random(),
                randomString(),
                false,
                JbstUserEmailDetails.unnecessary(),
                new HashMap<>()
        );
    }

    public static JbstJwtUser randomSuperadminNotPersisted() {
        return new JbstJwtUser(
                null,
                JbstUserCreationOption.random(),
                Username.random(),
                Password.random(),
                true,
                randomZoneId(),
                getSimpleGrantedAuthorities(SUPERADMIN),
                Email.random(),
                randomString(),
                false,
                JbstUserEmailDetails.unnecessary(),
                new HashMap<>()
        );
    }

    @JsonIgnore
    public JbstRequestUserToken getRequestUserTokenAsEmailConfirmation() {
        return new JbstRequestUserToken(
                this.email,
                JbstUserTokenType.EMAIL_CONFIRMATION
        );
    }

    @JsonIgnore
    public JbstRequestUserToken getRequestUserTokenAsPasswordReset() {
        return new JbstRequestUserToken(
                this.email,
                JbstUserTokenType.PASSWORD_RESET
        );
    }

    @JsonIgnore
    public JbstJwtTokenCreationParams getJwtTokenCreationParams() {
        return new JbstJwtTokenCreationParams(
                this.username,
                this.authorities,
                this.zoneId
        );
    }

    @SuppressWarnings("unused")
    public boolean hasAllAuthorities(Set<SimpleGrantedAuthority> authorities) {
        return this.authorities.containsAll(authorities);
    }
}

