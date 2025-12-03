package jbst.foundation.domain.properties.configs.security;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.properties.base.JbstPropertyTimeAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static java.time.temporal.ChronoUnit.SECONDS;
import static jbst.foundation.domain.random.JbstRandom.randomString;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertySecurityCookies extends JbstProperty {
    @JbstPropertyMandatory
    private final String domain;
    @JbstPropertyMandatory
    private final JbstPropertyTimeAmount jwtAccessTokenCookieCreationLatency;

    public static JbstPropertySecurityCookies hardcoded() {
        return new JbstPropertySecurityCookies(JbstConstants.Domains.HARDCODED, new JbstPropertyTimeAmount(5L, SECONDS));
    }

    public static JbstPropertySecurityCookies random() {
        return new JbstPropertySecurityCookies(randomString(), JbstPropertyTimeAmount.random());
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.BRANCH;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonLeaf() {
        return "cookies";
    }
}
