package jbst.foundation.domain.properties.configs.security;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyOptional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.domain.random.JbstRandom.randomString;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertySecurityUsersTokens extends JbstProperty {
    @JbstPropertyOptional
    private String webclientMagicLinkPath;
    @JbstPropertyOptional
    private String webclientEmailConfirmationRedirectPath;
    @JbstPropertyOptional
    private String webclientPasswordResetPath;

    public static JbstPropertySecurityUsersTokens fixed() {
        return new JbstPropertySecurityUsersTokens(
                "/magic-link",
                "/email-confirmation",
                "/password-reset"
        );
    }

    public static JbstPropertySecurityUsersTokens random() {
        return new JbstPropertySecurityUsersTokens(
                randomString(),
                randomString(),
                randomString()
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
        return "users-tokens";
    }
}
