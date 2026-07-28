package jbst.foundation.domain.properties.configs.security;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
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
import static jbst.foundation.domain.asserts.JbstAsserts.assertFalseOrThrow;
import static jbst.foundation.domain.constants.JbstConstants.Logs.PREFIX;
import static jbst.foundation.domain.random.JbstRandom.randomEnum;
import static jbst.foundation.domain.random.JbstRandom.randomString;

@Slf4j
// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertySecurityJWT extends JbstProperty {
    @JbstPropertyMandatory
    private final String secretKey;
    @JbstPropertyMandatory
    private final JbstJwtTokenStorageMethod storageMethod;
    @JbstPropertyMandatory
    private final JbstPropertyJwtToken accessToken;
    @JbstPropertyMandatory
    private final JbstPropertyJwtToken refreshToken;

    public static JbstPropertySecurityJWT fixed() {
        return new JbstPropertySecurityJWT(
                "nbVwWebIpNnZ1rsNZFmkAQGiOZAijWtSt5X6FZx/qHA=",
                JbstJwtTokenStorageMethod.COOKIES,
                new JbstPropertyJwtToken(new JbstPropertyTimeAmount(30L, SECONDS), "ajwt", null),
                new JbstPropertyJwtToken(new JbstPropertyTimeAmount(12L, HOURS), "rjwt", null)
        );
    }

    public static JbstPropertySecurityJWT random() {
        return new JbstPropertySecurityJWT(
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
        return "jwt";
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
