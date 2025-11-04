package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.enums.JbstJwtTokenStorageMethod;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.properties.annotations.JbstPropertyOptional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Objects.nonNull;
import static jbst.foundation.domain.asserts.Asserts.assertFalseOrThrow;
import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;
import static jbst.foundation.utilities.random.RandomUtility.randomString;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyJwtToken extends JbstProperty {
    @JbstPropertyMandatory
    private final JbstPropertyTimeAmount expiration;
    @JbstPropertyOptional
    private String cookieKey;
    @JbstPropertyOptional
    private String headerKey;

    public static JbstPropertyJwtToken hardcoded() {
        return new JbstPropertyJwtToken(new JbstPropertyTimeAmount(12L, HOURS), "cookieJWT", null);
    }

    public static JbstPropertyJwtToken random() {
        return randomBoolean() ? randomCookieBasedToken() : randomHeaderBasedToken();
    }

    public static JbstPropertyJwtToken randomCookieBasedToken() {
        return new JbstPropertyJwtToken(JbstPropertyTimeAmount.random(), randomString(), null);
    }

    public static JbstPropertyJwtToken randomHeaderBasedToken() {
        return new JbstPropertyJwtToken(JbstPropertyTimeAmount.random(), null, randomString());
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

    @Override
    public void assertPropertiesAsLeaf(String parentTreeName) {
        super.assertPropertiesAsLeaf(parentTreeName);
        assertFalseOrThrow(
                nonNull(this.cookieKey) && nonNull(this.headerKey),
                "[JwtToken]: 'cookie-key' or 'header-key' expected to be provided"
        );
    }

    public String getKey(JbstJwtTokenStorageMethod method) {
        if (method.isCookies()) {
            return this.cookieKey;
        }
        if (method.isHeaders()) {
            return this.headerKey;
        }
        throw new IllegalArgumentException();
    }
}
