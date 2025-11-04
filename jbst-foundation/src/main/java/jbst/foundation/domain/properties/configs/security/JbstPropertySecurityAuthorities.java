package jbst.foundation.domain.properties.configs.security;

import jbst.foundation.domain.base.AbstractAuthority;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.properties.base.JbstPropertyAuthority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Set;
import java.util.stream.Collectors;

import static jbst.foundation.utilities.random.RandomUtility.randomString;
import static jbst.foundation.utilities.random.RandomUtility.randomStringsAsSet;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertySecurityAuthorities extends JbstProperty {
    @JbstPropertyMandatory
    private final String packageName;
    @JbstPropertyMandatory
    private final Set<JbstPropertyAuthority> authorities;

    public static JbstPropertySecurityAuthorities hardcoded() {
        return new JbstPropertySecurityAuthorities(
                "jbst.foundation",
                Set.of(
                        new JbstPropertyAuthority(AbstractAuthority.SUPERADMIN),
                        new JbstPropertyAuthority(AbstractAuthority.INVITATIONS_READ),
                        new JbstPropertyAuthority(AbstractAuthority.INVITATIONS_WRITE),
                        new JbstPropertyAuthority(AbstractAuthority.PROMETHEUS_READ),
                        new JbstPropertyAuthority("admin"),
                        new JbstPropertyAuthority("user")
                )
        );
    }

    public static JbstPropertySecurityAuthorities random() {
        return new JbstPropertySecurityAuthorities(
                randomString(),
                randomStringsAsSet(3).stream().map(JbstPropertyAuthority::new).collect(Collectors.toSet())
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

    public Set<String> getAllAuthoritiesValues() {
        return this.authorities.stream().map(JbstPropertyAuthority::getValue).collect(Collectors.toSet());
    }

    public Set<String> getAvailableAuthorities() {
        return this.authorities.stream()
                .map(JbstPropertyAuthority::getValue)
                .filter(authority -> !AbstractAuthority.SUPERADMIN.equals(authority))
                .collect(Collectors.toSet());
    }
}
