package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.annotations.AbstractProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.NonMandatoryProperty;
import jbst.foundation.utilities.random.RandomUtility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.time.ZoneId;
import java.util.Set;

import static java.lang.Boolean.TRUE;
import static java.util.Objects.nonNull;
import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;
import static jbst.foundation.utilities.random.RandomUtility.randomStringsAsSet;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class UserOnInit extends AbstractProperty {
    @MandatoryProperty
    private final Username username;
    @MandatoryProperty
    private final Password password;
    @MandatoryProperty
    private final ZoneId zoneId;
    @NonMandatoryProperty
    private String email;
    @NonMandatoryProperty
    private final Boolean passwordChangeRequired;
    @MandatoryProperty
    private final Set<String> authorities;

    public static UserOnInit hardcoded() {
        return new UserOnInit(
                Username.hardcoded(),
                Password.hardcoded(),
                UKRAINE,
                Email.hardcoded().value(),
                false,
                Set.of("user", "admin")
        );
    }

    public static UserOnInit random() {
        return new UserOnInit(
                Username.random(),
                Password.random(),
                RandomUtility.randomZoneId(),
                Email.random().value(),
                randomBoolean(),
                randomStringsAsSet(3)
        );
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

    public Email getEmailOrNull() {
        return nonNull(this.email) ? Email.of(this.email) : null;
    }

    public boolean isPasswordChangeRequired() {
        return TRUE.equals(this.passwordChangeRequired);
    }
}
