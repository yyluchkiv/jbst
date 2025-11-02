package jbst.foundation.domain.properties.configs.security;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.JbstPropertyTimeAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static java.time.temporal.ChronoUnit.SECONDS;
import static jbst.foundation.utilities.random.RandomUtility.randomString;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class CookiesConfigs extends JbstProperty {
    @MandatoryProperty
    private final String domain;
    @MandatoryProperty
    private final JbstPropertyTimeAmount jwtAccessTokenCookieCreationLatency;

    public static CookiesConfigs hardcoded() {
        return new CookiesConfigs(JbstConstants.Domains.HARDCODED, new JbstPropertyTimeAmount(5L, SECONDS));
    }

    public static CookiesConfigs random() {
        return new CookiesConfigs(randomString(), JbstPropertyTimeAmount.random());
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
        return "cookies-configs";
    }
}
