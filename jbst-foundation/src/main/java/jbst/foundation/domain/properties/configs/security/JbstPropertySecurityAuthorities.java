package jbst.foundation.domain.properties.configs.security;

import jbst.foundation.domain.base.AbstractAuthority;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Set;
import java.util.stream.Collectors;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertySecurityAuthorities extends JbstProperty {
    @JbstPropertyMandatory
    private final String packageName;
    @JbstPropertyMandatory
    private final Set<String> values;

    public static JbstPropertySecurityAuthorities fixed() {
        return new JbstPropertySecurityAuthorities(
                "jbst.foundation",
                Set.of(
                        AbstractAuthority.SUPERADMIN,
                        AbstractAuthority.INVITATIONS_READ,
                        AbstractAuthority.INVITATIONS_WRITE,
                        AbstractAuthority.PROMETHEUS_READ,
                        "admin",
                        "user"
                )
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

    public Set<String> getAvailableAuthorities() {
        return this.values.stream()
                .filter(authority -> !AbstractAuthority.SUPERADMIN.equals(authority))
                .collect(Collectors.toSet());
    }
}
