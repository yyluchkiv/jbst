package jbst.foundation.services.base;

import jbst.foundation.configurations.TestJbstConfigurationPropertiesHardcoded;
import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.emails.JbstEmails;
import jbst.foundation.domain.functions.JbstFunctionAccountAccessed;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.configs.JbstPropertySecurity;
import jbst.foundation.services.JbstEmailService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Set;

import static java.time.LocalDateTime.parse;
import static java.time.ZoneOffset.UTC;
import static jbst.foundation.domain.constants.JbstConstants.DateTimeFormatters.DTF11;
import static jbst.foundation.domain.enums.JbstAccountAccessMethod.SESSION_TOKEN;
import static jbst.foundation.domain.enums.JbstAccountAccessMethod.USERNAME_PASSWORD;
import static jbst.foundation.domain.time.JbstTime.getTimestamp;
import static jbst.foundation.domain.time.TimestampUtility.getCurrentTimestamp;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstUsersEmailsServiceTest {

    @Configuration
    @Import({
            TestJbstConfigurationPropertiesHardcoded.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final ResourceLoader resourceLoader;
        private final JbstProperties jbstProperties;

        @Bean
        JbstEmailService emailService() {
            return mock(JbstEmailService.class);
        }

        @Bean
        ServerProperties serverProperties() {
            var serverProperties = mock(ServerProperties.class);
            var servlet = mock(ServerProperties.Servlet.class);
            when(servlet.getContextPath()).thenReturn("/api");
            when(serverProperties.getServlet()).thenReturn(servlet);
            return serverProperties;
        }

        @Bean
        public JbstUsersEmailsService userEmailService() {
            return new JbstUsersEmailsService(
                    this.resourceLoader,
                    this.emailService(),
                    this.jbstProperties,
                    this.serverProperties()
            );
        }
    }

    // Services
    private final JbstEmailService emailService;
    // Properties
    private final JbstProperties jbstProperties;

    private final JbstUsersEmailsService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.emailService
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.emailService
        );
    }

    @RepeatedTest(5)
    void getSubjectTest() {
        // Act
        var subject = this.componentUnderTest.getSubjectV1("Account Accessed");

        // Assert
        assertThat(subject)
                .startsWith("[jbst.com] Account Accessed | ")
                .endsWith(" (UTC)");
        subject = subject.replace("[jbst.com] Account Accessed | ", "");
        subject = subject.replace(" (UTC)", "");
        var timestamp = getTimestamp(parse(subject, DTF11), ZoneOffset.UTC);
        assertThat(getCurrentTimestamp() - timestamp).isBetween(0L, 2000L);
    }

    @Test
    void executeMagicLink() {
        // Arrange
        var userToken = JbstUserToken.hardcodedMagicLink();

        // Act
        this.componentUnderTest.executeMagicLink(userToken);

        // Assert
        ArgumentCaptor<JbstEmails.HTML> emailHTMLAC = ArgumentCaptor.forClass(JbstEmails.HTML.class);
        verify(this.emailService).sendHTML(emailHTMLAC.capture());
        var emailHTML = emailHTMLAC.getValue();
        assertThat(emailHTML.to()).isEqualTo(Set.of(userToken.email().value()));
        assertThat(emailHTML.subject()).startsWith("[jbst.com] Secure Link | ");
        assertThat(emailHTML.templateName()).isEqualTo("jbst-magic-link");
        assertThat(emailHTML.templateVariables())
                .hasSize(4)
                .containsEntry("version", this.jbstProperties.getApp().getMaven().getVersion())
                .containsEntry("email", userToken.email().value())
                .containsEntry("magicLink", "http://127.0.0.1:3000/magic-link?token=" + userToken.value())
                .containsEntry("year", LocalDate.now(UTC).getYear());
    }

    @Test
    void executeEmailConfirmation() {
        // Arrange
        var userToken = JbstUserToken.hardcodedEmailConfirmation();

        // Act
        this.componentUnderTest.executeEmailConfirmation(userToken);

        // Assert
        ArgumentCaptor<JbstEmails.HTML> emailHTMLAC = ArgumentCaptor.forClass(JbstEmails.HTML.class);
        verify(this.emailService).sendHTML(emailHTMLAC.capture());
        var emailHTML = emailHTMLAC.getValue();
        assertThat(emailHTML.to()).isEqualTo(Set.of(userToken.email().value()));
        assertThat(emailHTML.subject()).startsWith("[jbst.com] Email Confirmation | ");
        assertThat(emailHTML.templateName()).isEqualTo("jbst-email-confirmation");
        assertThat(emailHTML.templateVariables())
                .hasSize(4)
                .containsEntry("version", this.jbstProperties.getApp().getMaven().getVersion())
                .containsEntry("email", userToken.email().value())
                .containsEntry("emailConfirmationLink", "http://127.0.0.1:3002/api/jbst/security/tokens/email/confirm?token=" + userToken.value())
                .containsEntry("year", LocalDate.now(UTC).getYear());
    }

    @Test
    void executePasswordReset() {
        // Arrange
        var userToken = JbstUserToken.hardcodedPasswordReset();

        // Act
        this.componentUnderTest.executePasswordReset(userToken);

        // Assert
        ArgumentCaptor<JbstEmails.HTML> emailHTMLAC = ArgumentCaptor.forClass(JbstEmails.HTML.class);
        verify(this.emailService).sendHTML(emailHTMLAC.capture());
        var emailHTML = emailHTMLAC.getValue();
        assertThat(emailHTML.to()).isEqualTo(Set.of(userToken.email().value()));
        assertThat(emailHTML.subject()).startsWith("[jbst.com] Password Reset | ");
        assertThat(emailHTML.templateName()).isEqualTo("jbst-password-reset");
        assertThat(emailHTML.templateVariables())
                .hasSize(4)
                .containsEntry("version", this.jbstProperties.getApp().getMaven().getVersion())
                .containsEntry("email", userToken.email().value())
                .containsEntry("resetPasswordLink", "http://127.0.0.1:3000/password-reset?token=" + userToken.value())
                .containsEntry("year", LocalDate.now(UTC).getYear());
    }

    @Test
    void executeAuthenticationLoginDisabled() {
        // Arrange
        this.jbstProperties.setSecurity(JbstPropertySecurity.disabledUsersEmails());

        // Act
        this.componentUnderTest.executeAccountAccessed(JbstFunctionAccountAccessed.hardcoded(USERNAME_PASSWORD));

        // Assert
        // no actions + revert
        this.jbstProperties.setSecurity(JbstPropertySecurity.hardcoded());
    }

    @Test
    void executeAuthenticationLogin() {
        // Act
        this.componentUnderTest.executeAccountAccessed(JbstFunctionAccountAccessed.hardcoded(USERNAME_PASSWORD));

        // Assert
        ArgumentCaptor<JbstEmails.HTML> emailHTMLAC = ArgumentCaptor.forClass(JbstEmails.HTML.class);
        verify(this.emailService).sendHTML(emailHTMLAC.capture());
        var emailHTML = emailHTMLAC.getValue();
        assertThat(emailHTML.to()).isEqualTo(Set.of(Email.hardcoded().value()));
        assertThat(emailHTML.subject()).startsWith("[jbst.com] Account Accessed | ");
        assertThat(emailHTML.templateName()).isEqualTo("jbst-account-accessed");
        assertThat(emailHTML.templateVariables())
                .hasSize(8)
                .containsEntry("version", this.jbstProperties.getApp().getMaven().getVersion())
                .containsEntry("year", LocalDate.now(UTC).getYear())
                .containsEntry("username", Username.hardcoded().value())
                .containsEntry("accessMethod", USERNAME_PASSWORD.getValue())
                .containsEntry("where", "🇺🇦 Ukraine, Lviv")
                .containsEntry("what", "Chrome, macOS on Desktop")
                .containsEntry("ipAddress", "127.0.0.1")
                .containsEntry("webclientURL", "http://127.0.0.1:3000");
    }

    @Test
    void executeSessionRefreshedDisabled() {
        // Arrange
        this.jbstProperties.setSecurity(JbstPropertySecurity.disabledUsersEmails());

        // Act
        this.componentUnderTest.executeAccountAccessed(JbstFunctionAccountAccessed.hardcoded(SESSION_TOKEN));

        // Assert
        // no actions + revert
        this.jbstProperties.setSecurity(JbstPropertySecurity.hardcoded());
    }

    @Test
    void executeSessionRefreshed() {
        // Act
        this.componentUnderTest.executeAccountAccessed(JbstFunctionAccountAccessed.hardcoded(SESSION_TOKEN));

        // Assert
        ArgumentCaptor<JbstEmails.HTML> emailHTMLAC = ArgumentCaptor.forClass(JbstEmails.HTML.class);
        verify(this.emailService).sendHTML(emailHTMLAC.capture());
        var emailHTML = emailHTMLAC.getValue();
        assertThat(emailHTML.to()).isEqualTo(Set.of(Email.hardcoded().value()));
        assertThat(emailHTML.subject()).startsWith("[jbst.com] Account Accessed | ");
        assertThat(emailHTML.templateName()).isEqualTo("jbst-account-accessed");
        assertThat(emailHTML.templateVariables())
                .hasSize(8)
                .containsEntry("version", this.jbstProperties.getApp().getMaven().getVersion())
                .containsEntry("year", LocalDate.now(UTC).getYear())
                .containsEntry("username", Username.hardcoded().value())
                .containsEntry("accessMethod", SESSION_TOKEN.getValue())
                .containsEntry("where", "🇺🇦 Ukraine, Lviv")
                .containsEntry("what", "Chrome, macOS on Desktop")
                .containsEntry("ipAddress", "127.0.0.1")
                .containsEntry("webclientURL", "http://127.0.0.1:3000");
    }
}
