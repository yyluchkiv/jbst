package jbst.iam.domain.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.iam.domain.db.UserEmailDetails;
import jbst.iam.domain.dto.requests.RequestUserToken;
import jbst.iam.domain.enums.UserCreationOption;
import jbst.iam.domain.enums.UserTokenType;
import jbst.iam.domain.identifiers.UserId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.ZoneId;
import java.util.*;

import static jbst.foundation.domain.base.AbstractAuthority.*;
import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static jbst.foundation.utilities.random.RandomUtility.*;
import static jbst.foundation.utilities.spring.SpringAuthoritiesUtility.getSimpleGrantedAuthorities;

public record JwtUser(
        UserId id,
        UserCreationOption creationOption,
        Username username,
        Password password,
        ZoneId zoneId,
        Set<SimpleGrantedAuthority> authorities,
        Email email,
        String name,
        boolean passwordChangeRequired,
        UserEmailDetails emailDetails,
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
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static JwtUser hardcoded() {
        return hardcoded(
                getSimpleGrantedAuthorities("user")
        );
    }

    public static JwtUser hardcoded(Set<SimpleGrantedAuthority> authorities) {
        return new JwtUser(
                UserId.hardcoded(),
                UserCreationOption.hardcoded(),
                Username.hardcoded(),
                Password.hardcoded(),
                UKRAINE,
                authorities,
                Email.hardcoded(),
                "",
                false,
                UserEmailDetails.confirmed(),
                new HashMap<>()
        );
    }

    public static JwtUser hardcoded(
            Email email,
            UserEmailDetails emailDetails
    ) {
        return new JwtUser(
                UserId.hardcoded(),
                UserCreationOption.hardcoded(),
                Username.hardcoded(),
                Password.hardcoded(),
                UKRAINE,
                new HashSet<>(),
                email,
                "",
                false,
                emailDetails,
                new HashMap<>()
        );
    }

    public static JwtUser hardcoded(Map<String, Object> attributes) {
        var user = JwtUser.hardcoded();
        user.attributes().putAll(attributes);
        return user;
    }

    public static JwtUser random() {
        return new JwtUser(
                UserId.random(),
                UserCreationOption.random(),
                Username.random(),
                Password.random(),
                randomZoneId(),
                Set.of(
                        new SimpleGrantedAuthority(randomElement(List.of(SUPERADMIN, INVITATIONS_READ, INVITATIONS_WRITE)))
                ),
                Email.random(),
                randomString(),
                randomBoolean(),
                UserEmailDetails.random(),
                new HashMap<>(
                        Map.of(
                            randomString(), randomString(),
                            randomString(), randomInteger()
                        )
                )
        );
    }

    public static JwtUser randomSuperadmin() {
        return new JwtUser(
                UserId.random(),
                UserCreationOption.random(),
                Username.random(),
                Password.random(),
                randomZoneId(),
                getSimpleGrantedAuthorities(SUPERADMIN),
                Email.random(),
                randomString(),
                false,
                UserEmailDetails.unnecessary(),
                new HashMap<>()
        );
    }

    public static JwtUser randomSuperadminNotPersisted() {
        return new JwtUser(
                null,
                UserCreationOption.random(),
                Username.random(),
                Password.random(),
                randomZoneId(),
                getSimpleGrantedAuthorities(SUPERADMIN),
                Email.random(),
                randomString(),
                false,
                UserEmailDetails.unnecessary(),
                new HashMap<>()
        );
    }

    @JsonIgnore
    public RequestUserToken getRequestUserTokenAsEmailConfirmation() {
        return new RequestUserToken(
                this.email,
                this.username,
                UserTokenType.EMAIL_CONFIRMATION
        );
    }

    @JsonIgnore
    public RequestUserToken getRequestUserTokenAsPasswordReset() {
        return new RequestUserToken(
                this.email,
                this.username,
                UserTokenType.PASSWORD_RESET
        );
    }

    @JsonIgnore
    public JwtTokenCreationParams getJwtTokenCreationParams() {
        return new JwtTokenCreationParams(
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

