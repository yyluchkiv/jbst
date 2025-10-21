package jbst.foundation.domain.properties.configs.security.jwt;

import jbst.foundation.domain.asserts.Asserts;
import jbst.foundation.domain.asserts.ConsoleAsserts;
import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.JwtToken;
import jbst.foundation.domain.properties.base.JwtTokenStorageMethod;
import jbst.foundation.domain.properties.base.TimeAmount;
import jbst.foundation.domain.properties.configs.AbstractPropertiesConfigs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static jbst.foundation.utilities.random.RandomUtility.randomEnum;
import static jbst.foundation.utilities.random.RandomUtility.randomString;

@Slf4j
// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JwtTokensConfigs extends AbstractPropertiesConfigs {
    @MandatoryProperty
    private final String secretKey;
    @MandatoryProperty
    private final JwtTokenStorageMethod storageMethod;
    @MandatoryProperty
    private final JwtToken accessToken;
    @MandatoryProperty
    private final JwtToken refreshToken;

    public static JwtTokensConfigs hardcoded() {
        return new JwtTokensConfigs(
                "nbVwWebIpNnZ1rsNZFmkAQGiOZAijWtSt5X6FZx/qHA=",
                JwtTokenStorageMethod.COOKIES,
                new JwtToken(new TimeAmount(30L, SECONDS), "ajwt", null),
                new JwtToken(new TimeAmount(12L, HOURS), "rjwt", null)
        );
    }

    public static JwtTokensConfigs random() {
        return new JwtTokensConfigs(
                randomString(),
                randomEnum(JwtTokenStorageMethod.class),
                JwtToken.random(),
                JwtToken.random()
        );
    }

    @Override
    public boolean isParentPropertiesNode() {
        return false;
    }

    @Override
    public PropertyId getPropertyId() {
        return new PropertyId("jwt-tokens-configs");
    }

    @Override
    public void assertProperties(PropertyId propertyId) {
        super.assertProperties(propertyId);
        if (this.storageMethod.isCookies()) {
            Asserts.assertFalseOrThrow(
                    this.accessToken.getCookieKey().equals(this.refreshToken.getCookieKey()),
                    "Please make sure \"%s.accessToken.cookieKey\" and \"%s.refreshToken.cookieKey\" are different".formatted(
                            ConsoleAsserts.RED_TEXT.format(propertyId.value()),
                            ConsoleAsserts.RED_TEXT.format(propertyId.value())
                    )
            );
        }
        if (this.storageMethod.isHeaders()) {
            Asserts.assertFalseOrThrow(
                    this.accessToken.getHeaderKey().equals(this.refreshToken.getHeaderKey()),
                    "Please make sure \"%s.accessToken.headerKey\" and \"%s.refreshToken.headerKey\" are different".formatted(
                            ConsoleAsserts.RED_TEXT.format(propertyId.value()),
                            ConsoleAsserts.RED_TEXT.format(propertyId.value())
                    )
            );
        }
        LOGGER.info(
                "{} JWT tokens storage method — {}: \"{}\" and \"{}\"",
                JbstConstants.Logs.PREFIX,
                this.storageMethod,
                this.accessToken.getKey(this.storageMethod),
                this.refreshToken.getKey(this.storageMethod)
        );
    }
}
