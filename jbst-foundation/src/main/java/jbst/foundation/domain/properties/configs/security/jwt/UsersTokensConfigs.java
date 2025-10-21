package jbst.foundation.domain.properties.configs.security.jwt;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.NonMandatoryProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.utilities.random.RandomUtility.randomString;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class UsersTokensConfigs extends JbstProperty {
    @NonMandatoryProperty
    private String webclientMagicLinkPath;
    @NonMandatoryProperty
    private String webclientEmailConfirmationRedirectPath;
    @NonMandatoryProperty
    private String webclientPasswordResetPath;

    public static UsersTokensConfigs hardcoded() {
        return new UsersTokensConfigs(
                "/magic-link",
                "/email-confirmation",
                "/password-reset"
        );
    }

    public static UsersTokensConfigs random() {
        return new UsersTokensConfigs(
                randomString(),
                randomString(),
                randomString()
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
        return "users-tokens-configs";
    }
}
