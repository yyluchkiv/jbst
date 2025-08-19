package jbst.iam.utils.impl;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.services.emails.domain.EmailHTML;
import jbst.iam.domain.db.UserToken;
import jbst.iam.domain.functions.FunctionAccountAccessed;
import jbst.iam.utils.UserEmailUtils;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

import static java.time.ZoneOffset.UTC;
import static jbst.foundation.domain.constants.JbstConstants.DateTimeFormatters.DTF11;
import static jbst.foundation.utilities.time.LocalDateUtility.now;
import static jbst.iam.domain.enums.AccountAccessMethod.SECURITY_TOKEN;
import static jbst.iam.domain.enums.AccountAccessMethod.USERNAME_PASSWORD;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserEmailUtilsImpl implements UserEmailUtils {

    // Resources
    private final ResourceLoader resourceLoader;
    // Properties
    private final JbstProperties jbstProperties;
    private final ServerProperties serverProperties;

    @Override
    public String getSubject(@NotNull String eventName) {
        var prefix = this.jbstProperties.getSecurityJwtConfigs().getUsersEmailsConfigs().getSubjectPrefix();
        var time = LocalDateTime.now(UTC).format(DTF11) + " (UTC)";
        return prefix + " " + eventName + " at " + time;
    }

    @Override
    public EmailHTML getAccountAccessedHTML(@NotNull FunctionAccountAccessed function) {
        var templateName = "jbst-account-accessed";
        if (USERNAME_PASSWORD.equals(function.accountAccessMethod())) {
            templateName = this.getServerOrFallbackJbstTemplateName(
                    "server-authentication-login",
                    "jbst-account-accessed"
            );
        } else if (SECURITY_TOKEN.equals(function.accountAccessMethod())) {
            templateName = this.getServerOrFallbackJbstTemplateName(
                    "server-session-refreshed",
                    "jbst-account-accessed"
            );
        }
        return EmailHTML.of(
                function.to(),
                this.getSubject("Account Accessed"),
                templateName,
                Map.ofEntries(
                        Map.entry("version", this.jbstProperties.getServerConfigs().getMavenConfigs().getVersion()),
                        Map.entry("year", now(UTC).getYear()),
                        Map.entry("username", function.username().value()),
                        Map.entry("accessMethod", function.accountAccessMethod().getValue()),
                        Map.entry("where", function.userRequestMetadata().getGeoLocation().getWhere()),
                        Map.entry("what", function.userRequestMetadata().getUserAgentDetails().getWhat()),
                        Map.entry("ipAddress", function.userRequestMetadata().getGeoLocation().getIpAddr()),
                        Map.entry("webclientURL", this.jbstProperties.getServerConfigs().getWebclientURL())
                )
        );
    }

    @Override
    public EmailHTML getEmailConfirmationHTML(@NotNull UserToken userToken) {
        return EmailHTML.of(
                userToken.email(),
                this.getSubject("Email Confirmation"),
                this.getServerOrFallbackJbstTemplateName(
                        "server-email-confirmation",
                        "jbst-email-confirmation"
                ),
                Map.ofEntries(
                        Map.entry("version", this.jbstProperties.getServerConfigs().getMavenConfigs().getVersion()),
                        Map.entry("year", now(UTC).getYear()),
                        Map.entry("email", userToken.email().value()),
                        Map.entry("emailConfirmationLink", this.jbstProperties.getEmailConfirmationLink(this.serverProperties, userToken.value()))
                )
        );
    }

    @Override
    public EmailHTML getPasswordResetHTML(@NotNull UserToken userToken) {
        return EmailHTML.of(
                userToken.email(),
                this.getSubject("Password Reset"),
                this.getServerOrFallbackJbstTemplateName(
                        "server-password-reset",
                        "jbst-password-reset"
                ),
                Map.ofEntries(
                        Map.entry("version", this.jbstProperties.getServerConfigs().getMavenConfigs().getVersion()),
                        Map.entry("year", now(UTC).getYear()),
                        Map.entry("email", userToken.email().value()),
                        Map.entry("resetPasswordLink", this.jbstProperties.getPasswordResetLink(userToken.value()))
                )
        );
    }

    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    private String getServerOrFallbackJbstTemplateName(String serverTemplateName, String jbstTemplateName) {
        var resource = this.resourceLoader.getResource("classpath:/email-templates/" + serverTemplateName + ".html");
        return resource.exists() ? serverTemplateName : jbstTemplateName;
    }
}
