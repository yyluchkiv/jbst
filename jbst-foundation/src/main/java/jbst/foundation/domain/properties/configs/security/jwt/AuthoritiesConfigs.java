package jbst.foundation.domain.properties.configs.security.jwt;

import jbst.foundation.domain.base.AbstractAuthority;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.AbstractJbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.Authority;
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
public class AuthoritiesConfigs extends AbstractJbstProperty {
    @MandatoryProperty
    private final String packageName;
    @MandatoryProperty
    private final Set<Authority> authorities;

    public static AuthoritiesConfigs hardcoded() {
        return new AuthoritiesConfigs(
                "jbst.foundation",
                Set.of(
                        new Authority(AbstractAuthority.SUPERADMIN),
                        new Authority(AbstractAuthority.INVITATIONS_READ),
                        new Authority(AbstractAuthority.INVITATIONS_WRITE),
                        new Authority(AbstractAuthority.PROMETHEUS_READ),
                        new Authority("admin"),
                        new Authority("user")
                )
        );
    }

    public static AuthoritiesConfigs random() {
        return new AuthoritiesConfigs(
                randomString(),
                randomStringsAsSet(3).stream().map(Authority::new).collect(Collectors.toSet())
        );
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonMandatory() {
        return JbstConstants.Symbols.DASH;
    }

    public Set<String> getAllAuthoritiesValues() {
        return this.authorities.stream().map(Authority::getValue).collect(Collectors.toSet());
    }

    public Set<String> getAvailableAuthorities() {
        return this.authorities.stream()
                .map(Authority::getValue)
                .filter(authority -> !AbstractAuthority.SUPERADMIN.equals(authority))
                .collect(Collectors.toSet());
    }
}
