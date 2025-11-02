package jbst.foundation.domain.properties.configs.security;

import jbst.foundation.domain.enums.JbstAccountAccessMethod;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.JbstPropertyCheckbox;
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
public class JbstPropertySecurityUsersEmails extends JbstProperty {
    @MandatoryProperty
    private final String subjectPrefix;
    @MandatoryProperty
    private final JbstPropertyCheckbox accountAccessedMagicLink;
    @MandatoryProperty
    private final JbstPropertyCheckbox accountAccessedUsernamePassword;
    @MandatoryProperty
    private final JbstPropertyCheckbox accountAccessedSessionToken;

    public static JbstPropertySecurityUsersEmails hardcoded() {
        return new JbstPropertySecurityUsersEmails(
                "[jbst.com]",
                JbstPropertyCheckbox.enabled(),
                JbstPropertyCheckbox.enabled(),
                JbstPropertyCheckbox.enabled()
        );
    }

    public static JbstPropertySecurityUsersEmails random() {
        return new JbstPropertySecurityUsersEmails(
                randomString(),
                JbstPropertyCheckbox.enabled(),
                JbstPropertyCheckbox.enabled(),
                JbstPropertyCheckbox.enabled()
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
        return "users-emails-configs";
    }

    public boolean isEnabled(JbstAccountAccessMethod method) {
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
