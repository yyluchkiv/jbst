package jbst.foundation.domain.properties.configs.security.jwt;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.JbstPropertyJwtToken;
import jbst.foundation.domain.enums.JbstJwtTokenStorageMethod;
import jbst.foundation.domain.properties.base.JbstPropertyTimeAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static jbst.foundation.domain.asserts.Asserts.assertFalseOrThrow;
import static jbst.foundation.domain.constants.JbstConstants.Logs.PREFIX;
import static jbst.foundation.utilities.random.RandomUtility.randomEnum;
import static jbst.foundation.utilities.random.RandomUtility.randomString;

@Slf4j
// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JwtTokensConfigs extends JbstProperty {
    @MandatoryProperty
    private final String secretKey;
    @MandatoryProperty
    private final JbstJwtTokenStorageMethod storageMethod;
    @MandatoryProperty
    private final JbstPropertyJwtToken accessToken;
    @MandatoryProperty
    private final JbstPropertyJwtToken refreshToken;

    public static JwtTokensConfigs hardcoded() {
        return new JwtTokensConfigs(
                "nbVwWebIpNnZ1rsNZFmkAQGiOZAijWtSt5X6FZx/qHA=",
                JbstJwtTokenStorageMethod.COOKIES,
                new JbstPropertyJwtToken(new JbstPropertyTimeAmount(30L, SECONDS), "ajwt", null),
                new JbstPropertyJwtToken(new JbstPropertyTimeAmount(12L, HOURS), "rjwt", null)
        );
    }

    public static JwtTokensConfigs random() {
        return new JwtTokensConfigs(
                randomString(),
                randomEnum(JbstJwtTokenStorageMethod.class),
                JbstPropertyJwtToken.random(),
                JbstPropertyJwtToken.random()
        );
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
        return "jwt-tokens-configs";
    }

    @Override
    public void assertProperties() {
        super.assertProperties();
        if (this.storageMethod.isCookies()) {
            assertFalseOrThrow(
                    this.accessToken.getCookieKey().equals(this.refreshToken.getCookieKey()),
                    "Please make sure access-token.cookie-key and refresh-token.cookie-key are different"
            );
        }
        if (this.storageMethod.isHeaders()) {
            assertFalseOrThrow(
                    this.accessToken.getHeaderKey().equals(this.refreshToken.getHeaderKey()),
                    "Please make sure access-token.header-key and refresh-token.header-key are different"
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
