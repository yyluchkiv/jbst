package jbst.foundation.domain.properties.configs.security.jwt;

import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.TimeAmount;
import jbst.foundation.domain.properties.configs.AbstractPropertiesConfigs;
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
public class CookiesConfigs extends AbstractPropertiesConfigs {
    @MandatoryProperty
    private final String domain;
    @MandatoryProperty
    private final TimeAmount jwtAccessTokenCookieCreationLatency;

    public static CookiesConfigs hardcoded() {
        return new CookiesConfigs(JbstConstants.Domains.HARDCODED, new TimeAmount(5L, SECONDS));
    }

    public static CookiesConfigs random() {
        return new CookiesConfigs(randomString(), TimeAmount.random());
    }

    @Override
    public boolean isParentPropertiesNode() {
        return false;
    }

    @Override
    public PropertyId getPropertyId() {
        return new PropertyId("cookies-configs");
    }
}
