package jbst.foundation.services.emails;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.emails.JbstEmails;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.configs.JbstPropertyEmails;
import jbst.foundation.services.JbstEmailService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static jakarta.mail.Message.RecipientType.TO;
import static jbst.foundation.domain.random.JbstRandom.randomString;
import static jbst.foundation.domain.random.JbstRandomEntities.entity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstEmailServiceEnabledTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        JbstProperties jbstProperties() {
            return mock(JbstProperties.class);
        }

        @Bean
        JavaMailSender javaMailSender() {
            return mock(JavaMailSender.class);
        }

        @Bean
        SpringTemplateEngine springTemplateEngine() {
            var templateEngine = new SpringTemplateEngine();
            templateEngine.addTemplateResolver(htmlTemplateResolver());
            return templateEngine;
        }

        @Bean
        SpringResourceTemplateResolver htmlTemplateResolver() {
            var emailTemplateResolver = new SpringResourceTemplateResolver();
            emailTemplateResolver.setPrefix("classpath:/tests-email-templates/");
            emailTemplateResolver.setSuffix(".html");
            emailTemplateResolver.setTemplateMode(TemplateMode.HTML);
            emailTemplateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
            return emailTemplateResolver;
        }

        @Bean
        JbstEmailService emailService() {
            return new JbstEmailServiceEnabled(
                    this.javaMailSender(),
                    this.springTemplateEngine(),
                    this.jbstProperties()
            );
        }
    }

    // Services
    private final JavaMailSender javaMailSender;
    // Properties
    private final JbstProperties jbstProperties;

    private final JbstEmailService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.javaMailSender,
                this.jbstProperties
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.javaMailSender,
                this.jbstProperties
        );
    }

    @Test
    void mainSendPlainDisabledTest() {
        // Arrange
        var to = Email.random().value();
        var subject = randomString();
        var message = randomString();
        var emails = JbstPropertyEmails.disabled();
        when(this.jbstProperties.getEmails()).thenReturn(emails);

        // Act
        this.componentUnderTest.sendPlain(new String[] { to }, subject, message);

        // Assert
        verify(this.jbstProperties).getEmails();
    }

    @Test
    void mainSendPlainEnabledTest() {
        // Arrange
        var to = Email.random().value();
        var from = Email.random().value();
        var subject = randomString();
        var message = randomString();
        var emails = JbstPropertyEmails.enabled(from);
        when(this.jbstProperties.getEmails()).thenReturn(emails);

        // Act
        this.componentUnderTest.sendPlain(new String[] { to }, subject, message);

        // Assert
        verify(this.jbstProperties).getEmails();
        var mailMessageAC = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(this.javaMailSender).send(mailMessageAC.capture());
        var simpleMailMessage = mailMessageAC.getValue();
        assertThat(simpleMailMessage.getTo()).isEqualTo(new String[] { to });
        assertThat(simpleMailMessage.getSubject()).isEqualTo(subject);
        assertThat(simpleMailMessage.getText()).isEqualTo(message);
        assertThat(simpleMailMessage.getFrom()).isEqualTo(from);
    }

    @Test
    void listSendPlainEnabledTest() {
        // Arrange
        var to = Email.random().value();
        var from = Email.random().value();
        var subject = randomString();
        var message = randomString();
        var emails = JbstPropertyEmails.enabled(from);
        when(this.jbstProperties.getEmails()).thenReturn(emails);

        // Act
        this.componentUnderTest.sendPlain(List.of(to), subject, message);

        // Assert
        verify(this.jbstProperties).getEmails();
        var mailMessageAC = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(this.javaMailSender).send(mailMessageAC.capture());
        var simpleMailMessage = mailMessageAC.getValue();
        assertThat(simpleMailMessage.getTo()).isEqualTo(new String[] { to });
        assertThat(simpleMailMessage.getSubject()).isEqualTo(subject);
        assertThat(simpleMailMessage.getText()).isEqualTo(message);
        assertThat(simpleMailMessage.getFrom()).isEqualTo(from);
    }

    @Test
    void setSendPlainEnabledTest() {
        // Arrange
        var to = Email.random().value();
        var from = Email.random().value();
        var subject = randomString();
        var message = randomString();
        var emails = JbstPropertyEmails.enabled(from);
        when(this.jbstProperties.getEmails()).thenReturn(emails);

        // Act
        this.componentUnderTest.sendPlain(Set.of(to), subject, message);

        // Assert
        verify(this.jbstProperties).getEmails();
        var mailMessageAC = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(this.javaMailSender).send(mailMessageAC.capture());
        var simpleMailMessage = mailMessageAC.getValue();
        assertThat(simpleMailMessage.getTo()).isEqualTo(new String[] { to });
        assertThat(simpleMailMessage.getSubject()).isEqualTo(subject);
        assertThat(simpleMailMessage.getText()).isEqualTo(message);
        assertThat(simpleMailMessage.getFrom()).isEqualTo(from);
    }

    @Test
    void sendPlainAttachmentDisabledTest() {
        // Arrange
        var data = entity(JbstEmails.AttachmentAndText.class);
        var emails = JbstPropertyEmails.disabled();
        when(this.jbstProperties.getEmails()).thenReturn(emails);

        // Act
        this.componentUnderTest.sendPlainAttachment(data);

        // Assert
        verify(this.jbstProperties).getEmails();
    }

    @Test
    void sendPlainAttachmentEnabledExceptionTest() throws MessagingException {
        // Arrange
        var data = entity(JbstEmails.AttachmentAndText.class);
        var from = Email.random().value();
        var emails = JbstPropertyEmails.enabled(from);
        var mimeMessage = mock(MimeMessage.class);
        doThrow(new MessagingException()).when(mimeMessage).setFrom(from);
        when(this.jbstProperties.getEmails()).thenReturn(emails);
        when(this.javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        this.componentUnderTest.sendPlainAttachment(data);

        // Assert
        verify(this.jbstProperties).getEmails();
        verify(this.javaMailSender).createMimeMessage();
    }

    @Test
    void sendPlainAttachmentEnabledTest() throws MessagingException, IOException {
        // Arrange
        var data = new JbstEmails.AttachmentAndText(
                Set.of(
                        "test1@" + JbstConstants.Domains.HARDCODED,
                        "test2@" + JbstConstants.Domains.HARDCODED
                ),
                "subject1",
                "message1",
                "attachment-file-name1",
                "attachment-message1"
        );
        var from = Email.random().value();
        var emails = JbstPropertyEmails.enabled(from);
        var mimeMessage = mock(MimeMessage.class);
        when(this.jbstProperties.getEmails()).thenReturn(emails);
        when(this.javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        this.componentUnderTest.sendPlainAttachment(data);

        // Assert
        verify(this.jbstProperties).getEmails();
        verify(this.javaMailSender).createMimeMessage();
        verify(mimeMessage).setFrom(from);
        verify(mimeMessage).setSubject("subject1");
        verify(mimeMessage).addRecipients(TO, "test1@" + JbstConstants.Domains.HARDCODED);
        verify(mimeMessage).addRecipients(TO, "test2@" + JbstConstants.Domains.HARDCODED);
        var mimeMultipartAC = ArgumentCaptor.forClass(MimeMultipart.class);
        verify(mimeMessage).setContent(mimeMultipartAC.capture());
        var multipart = mimeMultipartAC.getValue();
        assertThat(multipart.getCount()).isEqualTo(2);
        assertThat(multipart.getBodyPart(0).getContent()).isEqualTo("message1");
        assertThat(multipart.getBodyPart(1).getContent()).isEqualTo("attachment-message1");
        assertThat(multipart.getBodyPart(1).getFileName()).isEqualTo("attachment-file-name1");
        verify(this.javaMailSender).send(any(MimeMessage.class));
        verifyNoMoreInteractions(
                mimeMessage
        );
    }

    @Test
    void sendHTMLDisabledTest() {
        // Arrange
        var data = entity(JbstEmails.HTML.class);
        var emails = JbstPropertyEmails.disabled();
        when(this.jbstProperties.getEmails()).thenReturn(emails);

        // Act
        this.componentUnderTest.sendHTML(data);

        // Assert
        verify(this.jbstProperties).getEmails();
    }

    @Test
    void sendHTMLEnabledTest() {
        // Arrange
        var from = Email.random().value();
        Map<String, Object> templateVariables = Map.of(
                "param1", "key2",
                "param2", 2L
        );
        var data = new JbstEmails.HTML(
                Set.of(
                        "tests@" + JbstConstants.Domains.HARDCODED
                ),
                "subject1",
                "template1",
                templateVariables
        );
        var emails = JbstPropertyEmails.enabled(from);
        when(this.jbstProperties.getEmails()).thenReturn(emails);
        var message = mock(MimeMessage.class);
        when(this.javaMailSender.createMimeMessage()).thenReturn(message);

        // Act
        this.componentUnderTest.sendHTML(data);

        // Assert
        verify(this.jbstProperties).getEmails();
        ArgumentCaptor<MimeMessage> messageAC = ArgumentCaptor.forClass(MimeMessage.class);
        verify(this.javaMailSender).createMimeMessage();
        verify(this.javaMailSender).send(messageAC.capture());
    }
}
