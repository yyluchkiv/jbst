package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.properties.annotations.JbstPropertyOptional;
import jbst.foundation.domain.spring.JbstSpringAuthorities;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.ZoneId;
import java.util.Set;

import static java.lang.Boolean.TRUE;
import static java.util.Objects.nonNull;
import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyUserOnInit extends JbstProperty {
    @JbstPropertyMandatory
    private final Username username;
    @JbstPropertyMandatory
    private final Password password;
    @JbstPropertyMandatory
    private final ZoneId zoneId;
    @JbstPropertyMandatory
    private final Set<String> authorities;
    @JbstPropertyOptional
    private String email;
    @JbstPropertyOptional
    private Boolean passwordChangeRequired;

    public static JbstPropertyUserOnInit hardcoded() {
        return new JbstPropertyUserOnInit(
                Username.hardcoded(),
                Password.hardcoded(),
                UKRAINE,
                Set.of("user", "admin"),
                Email.hardcoded().value(),
                false
        );
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.LEAF;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonLeaf() {
        return JbstConstants.Symbols.DASH;
    }

    public Email getEmailOrNull() {
        return nonNull(this.email) ? Email.of(this.email) : null;
    }

    public boolean isPasswordChangeRequired() {
        return TRUE.equals(this.passwordChangeRequired);
    }

    public Set<SimpleGrantedAuthority> getSimpleGrantedAuthorities() {
        return JbstSpringAuthorities.getSimpleGrantedAuthorities(this.authorities);
    }
}
