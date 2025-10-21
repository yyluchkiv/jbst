package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.AbstractProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.NonMandatoryProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Objects.nonNull;
import static jbst.foundation.domain.asserts.Asserts.assertFalseOrThrow;
import static jbst.foundation.domain.constants.JbstConstants.JColor.RED_TEXT;
import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;
import static jbst.foundation.utilities.random.RandomUtility.randomString;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JwtToken extends AbstractProperty {
    @MandatoryProperty
    private final TimeAmount expiration;
    @NonMandatoryProperty
    private String cookieKey;
    @NonMandatoryProperty
    private String headerKey;

    public static JwtToken hardcoded() {
        return new JwtToken(new TimeAmount(12L, HOURS), "cookieJWT", null);
    }

    public static JwtToken random() {
        return randomBoolean() ? randomCookieBasedToken() : randomHeaderBasedToken();
    }

    public static JwtToken randomCookieBasedToken() {
        return new JwtToken(TimeAmount.random(), randomString(), null);
    }

    public static JwtToken randomHeaderBasedToken() {
        return new JwtToken(TimeAmount.random(), null, randomString());
    }

    @Override
    public boolean isParent() {
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

    @Override
    public void assertProperties(String propertyName) {
        super.assertProperties(propertyName);
        assertFalseOrThrow(
                nonNull(this.cookieKey) && nonNull(this.headerKey),
                "Attribute '%s' requires only 'cookie-key' or 'header-key' to be provided".formatted(
                        RED_TEXT.format(propertyName)
                )
        );
    }

    public String getKey(JwtTokenStorageMethod method) {
        if (method.isCookies()) {
            return this.cookieKey;
        }
        if (method.isHeaders()) {
            return this.headerKey;
        }
        throw new IllegalArgumentException();
    }
}
