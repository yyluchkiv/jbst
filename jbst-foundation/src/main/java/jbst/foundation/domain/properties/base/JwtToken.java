package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.asserts.ConsoleAsserts;
import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.NonMandatoryProperty;
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
public class JwtToken extends AbstractPropertyConfigs {
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
    public void assertProperties(PropertyId propertyId) {
        super.assertProperties(propertyId);
        assertFalseOrThrow(
                nonNull(this.cookieKey) && nonNull(this.headerKey),
                "Attribute \"%s\" requires only \"cookieKey\" or \"headerKey\" to be provided".formatted(
                        ConsoleAsserts.RED_TEXT.format(propertyId.value())
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
