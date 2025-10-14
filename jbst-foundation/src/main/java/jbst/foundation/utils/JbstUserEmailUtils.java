package jbst.foundation.utils;

import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.functions.FunctionAccountAccessed;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.services.emails.domain.EmailHTML;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

import static java.time.ZoneOffset.UTC;
import static jbst.foundation.domain.constants.JbstConstants.DateTimeFormatters.DTF11;
import static jbst.foundation.domain.enums.AccountAccessMethod.SECURITY_TOKEN;
import static jbst.foundation.domain.enums.AccountAccessMethod.USERNAME_PASSWORD;
import static jbst.foundation.utilities.time.LocalDateUtility.now;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstUserEmailUtils {

    // Resources
    private final ResourceLoader resourceLoader;
    // Properties
    private final JbstProperties jbstProperties;
    private final ServerProperties serverProperties;

    public final String getSubject(@NotNull String eventName) {
        var prefix = this.jbstProperties.getSecurityJwtConfigs().getUsersEmailsConfigs().getSubjectPrefix();
        var time = LocalDateTime.now(UTC).format(DTF11) + " (UTC)";
        return prefix + " " + eventName + " at " + time;
    }

    public final EmailHTML getAccountAccessedHTML(@NotNull FunctionAccountAccessed function) {
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

    public final EmailHTML getMagicLinkHTML(@NotNull JbstUserToken userToken) {
        return EmailHTML.of(
                userToken.email(),
                this.getSubject("Magic Link"),
                this.getServerOrFallbackJbstTemplateName(
                        "server-magic-link",
                        "jbst-magic-link"
                ),
                Map.ofEntries(
                        Map.entry("version", this.jbstProperties.getServerConfigs().getMavenConfigs().getVersion()),
                        Map.entry("year", now(UTC).getYear()),
                        Map.entry("email", userToken.email().value()),
                        Map.entry("magicLink", this.jbstProperties.getMagicLink(userToken.value()))
                )
        );
    }

    public final EmailHTML getEmailConfirmationHTML(@NotNull JbstUserToken userToken) {
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

    public final EmailHTML getPasswordResetHTML(@NotNull JbstUserToken userToken) {
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
