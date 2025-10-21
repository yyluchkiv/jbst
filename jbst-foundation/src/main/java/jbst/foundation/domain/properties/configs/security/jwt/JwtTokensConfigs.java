package jbst.foundation.domain.properties.configs.security.jwt;

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
import static jbst.foundation.domain.asserts.Asserts.assertFalseOrThrow;
import static jbst.foundation.domain.constants.JbstConstants.JColor.RED_TEXT;
import static jbst.foundation.domain.constants.JbstConstants.Logs.PREFIX;
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
    public String getPropertyName() {
        return"jwt-tokens-configs";
    }

    @Override
    public void assertProperties() {
        super.assertProperties();
        if (this.storageMethod.isCookies()) {
            assertFalseOrThrow(
                    this.accessToken.getCookieKey().equals(this.refreshToken.getCookieKey()),
                    "Please make sure %s.access-token.cookie-key and %s.refresh-token.cookie-key are different".formatted(
                            RED_TEXT.format(this.getPropertyName()),
                            RED_TEXT.format(this.getPropertyName())
                    )
            );
        }
        if (this.storageMethod.isHeaders()) {
            assertFalseOrThrow(
                    this.accessToken.getHeaderKey().equals(this.refreshToken.getHeaderKey()),
                    "Please make sure %s.access-token.header-key and %s.refresh-token.header-key are different".formatted(
                            RED_TEXT.format(this.getPropertyName()),
                            RED_TEXT.format(this.getPropertyName())
                    )
            );
        }
        LOGGER.info(
                "{} — tokens storage method — {}: {} and {}",
                PREFIX,
                this.storageMethod,
                this.accessToken.getKey(this.storageMethod),
                this.refreshToken.getKey(this.storageMethod)
        );
    }
}
