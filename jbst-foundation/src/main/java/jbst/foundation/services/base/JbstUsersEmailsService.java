package jbst.foundation.services.base;

import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.emails.JbstEmails;
import jbst.foundation.domain.functions.JbstFunctionAccountAccessed;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.tuples.Tuple2;
import jbst.foundation.services.JbstEmailService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

import static java.time.ZoneOffset.UTC;
import static jbst.foundation.domain.constants.JbstConstants.DateTimeFormatters.DTF11;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstUsersEmailsService {
    // Resources
    private final ResourceLoader resourceLoader;
    // Services
    private final JbstEmailService emailService;
    // Properties
    private final JbstProperties jbstProperties;
    private final ServerProperties serverProperties;

    public final void executeMagicLink(JbstUserToken userToken) {
        this.emailService.sendHTML(this.getMagicLinkHTML(userToken));
    }

    public final void executeEmailConfirmation(JbstUserToken userToken) {
        this.emailService.sendHTML(this.getEmailConfirmationHTML(userToken));
    }

    public final void executePasswordReset(JbstUserToken userToken) {
        this.emailService.sendHTML(this.getPasswordResetHTML(userToken));
    }

    public final void executeAccountAccessed(JbstFunctionAccountAccessed function) {
        if (!this.jbstProperties.getSecurity().getUsersEmails().isEnabled(function.accountAccessMethod())) {
            return;
        }
        this.emailService.sendHTML(this.getAccountAccessedHTML(function));
    }

    // =================================================================================================================
    // PRIVATE METHODS: Mails
    // =================================================================================================================
    private JbstEmails.HTML getAccountAccessedHTML(@NotNull JbstFunctionAccountAccessed function) {
        return JbstEmails.HTML.of(
                function.to(),
                this.getSubjectV1("Account Accessed"),
                function.getTemplateName(this.getTemplateNameFNC()),
                Map.ofEntries(
                        Map.entry("version", this.jbstProperties.getApp().getMaven().getVersion()),
                        Map.entry("year", LocalDate.now(UTC).getYear()),
                        Map.entry("username", function.username().value()),
                        Map.entry("accessMethod", function.accountAccessMethod().getValue()),
                        Map.entry("where", function.userRequestMetadata().getGeoLocation().getWhere()),
                        Map.entry("what", function.userRequestMetadata().getUserAgentDetails().getWhat()),
                        Map.entry("ipAddress", function.userRequestMetadata().getGeoLocation().getIpAddr()),
                        Map.entry("webclientURL", this.jbstProperties.getApp().getWebclientURL())
                )
        );
    }

    private JbstEmails.HTML getMagicLinkHTML(@NotNull JbstUserToken userToken) {
        return JbstEmails.HTML.of(
                userToken.email(),
                this.getSubjectV1("Secure Link"),
                this.getTemplateNameFNC().apply(new Tuple2<>(
                        "server-magic-link",
                        "jbst-magic-link"
                )),
                Map.ofEntries(
                        Map.entry("version", this.jbstProperties.getApp().getMaven().getVersion()),
                        Map.entry("year", LocalDate.now(UTC).getYear()),
                        Map.entry("email", userToken.email().value()),
                        Map.entry("magicLink", this.jbstProperties.getMagicLink(userToken.value()))
                )
        );
    }

    private JbstEmails.HTML getEmailConfirmationHTML(@NotNull JbstUserToken userToken) {
        return JbstEmails.HTML.of(
                userToken.email(),
                this.getSubjectV1("Email Confirmation"),
                this.getTemplateNameFNC().apply(new Tuple2<>(
                        "server-email-confirmation",
                        "jbst-email-confirmation"
                )),
                Map.ofEntries(
                        Map.entry("version", this.jbstProperties.getApp().getMaven().getVersion()),
                        Map.entry("year", LocalDate.now(UTC).getYear()),
                        Map.entry("email", userToken.email().value()),
                        Map.entry("emailConfirmationLink", this.jbstProperties.getEmailConfirmationLink(this.serverProperties, userToken.value()))
                )
        );
    }

    private JbstEmails.HTML getPasswordResetHTML(@NotNull JbstUserToken userToken) {
        return JbstEmails.HTML.of(
                userToken.email(),
                this.getSubjectV1("Password Reset"),
                this.getTemplateNameFNC().apply(new Tuple2<>(
                        "server-password-reset",
                        "jbst-password-reset"
                )),
                Map.ofEntries(
                        Map.entry("version", this.jbstProperties.getApp().getMaven().getVersion()),
                        Map.entry("year", LocalDate.now(UTC).getYear()),
                        Map.entry("email", userToken.email().value()),
                        Map.entry("resetPasswordLink", this.jbstProperties.getPasswordResetLink(userToken.value()))
                )
        );
    }

    // =================================================================================================================
    // PRIVATE METHODS: Subject(s)
    // =================================================================================================================
    protected String getSubjectV1(@NotNull String subject) {
        return "%s %s | %s".formatted(
                this.jbstProperties.getSecurity().getUsersEmails().getSubjectPrefix(),
                subject,
                LocalDateTime.now(UTC).format(DTF11) + " (UTC)"
        );
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
