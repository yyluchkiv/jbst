package jbst.foundation.domain.properties.configs.security.jwt;

import jbst.foundation.domain.enums.AccountAccessMethod;
import jbst.foundation.domain.properties.AbstractJbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.Checkbox;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static java.util.Objects.isNull;
import static jbst.foundation.utilities.random.RandomUtility.randomString;

@Slf4j
// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class UsersEmailsConfigs extends AbstractJbstProperty {
    @MandatoryProperty
    private final String subjectPrefix;
    @MandatoryProperty
    private final Checkbox accountAccessedMagicLink;
    @MandatoryProperty
    private final Checkbox accountAccessedUsernamePassword;
    @MandatoryProperty
    private final Checkbox accountAccessedSessionToken;

    public static UsersEmailsConfigs hardcoded() {
        return new UsersEmailsConfigs(
                "[jbst.com]",
                Checkbox.enabled(),
                Checkbox.enabled(),
                Checkbox.enabled()
        );
    }

    public static UsersEmailsConfigs random() {
        return new UsersEmailsConfigs(
                randomString(),
                Checkbox.enabled(),
                Checkbox.enabled(),
                Checkbox.enabled()
        );
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonMandatory() {
        return "users-emails-configs";
    }

    public boolean isEnabled(AccountAccessMethod method) {
        if (isNull(method)) {
            return false;
        }
        if (method.isMagicLink() && this.accountAccessedMagicLink.isEnabled()) {
            return true;
        }
        if (method.isUsernamePassword() && this.accountAccessedUsernamePassword.isEnabled()) {
            return true;
        }
        if (method.isSessionToken() && this.accountAccessedSessionToken.isEnabled()) {
            return true;
        }
        // fallback
        LOGGER.warn("Please double-check users-emails-configs to verify required {AccountAccessMethod + enable} configuration");
        return false;
    }
}
