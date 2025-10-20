package jbst.foundation.services.base;

import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.functions.FunctionAccountAccessed;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.tuples.Tuple2;
import jbst.foundation.services.UsersEmailsService;
import jbst.foundation.services.emails.domain.EmailHTML;
import jbst.foundation.services.emails.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

import static java.time.ZoneOffset.UTC;
import static jbst.foundation.domain.constants.JbstConstants.DateTimeFormatters.DTF11;
import static jbst.foundation.utilities.time.LocalDateUtility.now;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BaseUsersEmailsService implements UsersEmailsService {

    // Resources
    private final ResourceLoader resourceLoader;
    // Services
    private final EmailService emailService;
    // Properties
    private final JbstProperties jbstProperties;
    private final ServerProperties serverProperties;

    @Override
    public void executeMagicLink(JbstUserToken userToken) {
        this.emailService.sendHTML(this.getMagicLinkHTML(userToken));
    }

    @Override
    public void executeEmailConfirmation(JbstUserToken userToken) {
        this.emailService.sendHTML(this.getEmailConfirmationHTML(userToken));
    }

    @Override
    public void executePasswordReset(JbstUserToken userToken) {
        this.emailService.sendHTML(this.getPasswordResetHTML(userToken));
    }

    @Override
    public void executeAccountAccessed(FunctionAccountAccessed function) {
        if (!this.jbstProperties.getSecurityJwtConfigs().getUsersEmailsConfigs().isEnabled(function.accountAccessMethod())) {
            return;
        }
        this.emailService.sendHTML(this.getAccountAccessedHTML(function));
    }

    // =================================================================================================================
    // PRIVATE METHODS: Mails
    // =================================================================================================================
    private EmailHTML getAccountAccessedHTML(@NotNull FunctionAccountAccessed function) {
        return EmailHTML.of(
                function.to(),
                this.getSubject("Account Accessed"),
                function.getTemplateName(this.getTemplateNameFNC()),
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

    private EmailHTML getMagicLinkHTML(@NotNull JbstUserToken userToken) {
        return EmailHTML.of(
                userToken.email(),
                this.getSubject("Magic Link"),
                this.getTemplateNameFNC().apply(new Tuple2<>(
                        "server-magic-link",
                        "jbst-magic-link"
                )),
                Map.ofEntries(
                        Map.entry("version", this.jbstProperties.getServerConfigs().getMavenConfigs().getVersion()),
                        Map.entry("year", now(UTC).getYear()),
                        Map.entry("email", userToken.email().value()),
                        Map.entry("magicLink", this.jbstProperties.getMagicLink(userToken.value()))
                )
        );
    }

    private EmailHTML getEmailConfirmationHTML(@NotNull JbstUserToken userToken) {
        return EmailHTML.of(
                userToken.email(),
                this.getSubject("Email Confirmation"),
                this.getTemplateNameFNC().apply(new Tuple2<>(
                        "server-email-confirmation",
                        "jbst-email-confirmation"
                )),
                Map.ofEntries(
                        Map.entry("version", this.jbstProperties.getServerConfigs().getMavenConfigs().getVersion()),
                        Map.entry("year", now(UTC).getYear()),
                        Map.entry("email", userToken.email().value()),
                        Map.entry("emailConfirmationLink", this.jbstProperties.getEmailConfirmationLink(this.serverProperties, userToken.value()))
                )
        );
    }

    private EmailHTML getPasswordResetHTML(@NotNull JbstUserToken userToken) {
        return EmailHTML.of(
                userToken.email(),
                this.getSubject("Password Reset"),
                this.getTemplateNameFNC().apply(new Tuple2<>(
                        "server-password-reset",
                        "jbst-password-reset"
                )),
                Map.ofEntries(
                        Map.entry("version", this.jbstProperties.getServerConfigs().getMavenConfigs().getVersion()),
                        Map.entry("year", now(UTC).getYear()),
                        Map.entry("email", userToken.email().value()),
                        Map.entry("resetPasswordLink", this.jbstProperties.getPasswordResetLink(userToken.value()))
                )
        );
    }

    // =================================================================================================================
    // PRIVATE METHODS: Subject(s)
    // =================================================================================================================
    // TODO [YYL] maybe create different subjects
    // "[localhost] Magic Link at 20-10-2025 08:36:53 (UTC)" vs. "Secure link to log in to ? | 2025-09-01 07:20:06"
    protected String getSubject(@NotNull String eventName) {
        var prefix = this.jbstProperties.getSecurityJwtConfigs().getUsersEmailsConfigs().getSubjectPrefix();
        var time = LocalDateTime.now(UTC).format(DTF11) + " (UTC)";
        return prefix + " " + eventName + " at " + time;
    }

    // =================================================================================================================
    // PRIVATE METHODS: Spring
    // =================================================================================================================
    private Function<Tuple2<String, String>, String> getTemplateNameFNC() {
        return tuple2 -> {
            var serverTemplateName = tuple2.a();
            var jbstTemplateName = tuple2.b();
            var resource = this.resourceLoader.getResource("classpath:/email-templates/" + serverTemplateName + ".html");
            return resource.exists() ? serverTemplateName : jbstTemplateName;
        };
    }
}
