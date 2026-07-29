package jbst.foundation.services.base;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.enums.JbstAccountAccessMethod;
import jbst.foundation.domain.functions.JbstFunctionAccountAccessed;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.configs.JbstPropertyApp;
import jbst.foundation.domain.properties.configs.JbstPropertyEmails;
import jbst.foundation.domain.properties.configs.JbstPropertyMVC;
import jbst.foundation.domain.properties.configs.JbstPropertySecurity;
import jbst.foundation.services.JbstEmailService;
import jbst.foundation.services.emails.JbstEmailServiceEnabled;
import jbst.foundation.domain.concurrent.JbstSleep;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstUsersEmailsServiceConsoleTest {

    @Configuration
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final ResourceLoader resourceLoader;

        @Bean
        JbstProperties jbstProperties() {
            var jbstProperties = new JbstProperties();
            jbstProperties.setApp(JbstPropertyApp.fixed());
            jbstProperties.setMvc(JbstPropertyMVC.fixed());
            jbstProperties.setSecurity(JbstPropertySecurity.fixed());
            jbstProperties.setEmails(
                    new JbstPropertyEmails(
                            true,
                            "smtp.gmail.com",
                            587,
                            "jbst.com",
                            Username.of("-"),
                            Password.of("-")
                    )
            );
            return jbstProperties;
        }

        @Bean
        public JavaMailSender javaMailSender() {
            var emails = this.jbstProperties().getEmails();

            var mailSender = new JavaMailSenderImpl();
            mailSender.setHost(emails.getHost());
            mailSender.setPort(emails.getPort());

            mailSender.setUsername(emails.getUsername().value());
            mailSender.setPassword(emails.getPassword().value());

            var props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.debug", "false");

            return mailSender;
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
            emailTemplateResolver.setPrefix("classpath:/email-templates/");
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
                    this.jbstProperties(),
                    this.serverProperties()
            );
        }
    }

    private final JbstUsersEmailsService componentUnderTest;

    @Disabled
    @Test
    void executeMagicLink() {
        // Act
        this.componentUnderTest.executeMagicLink(
                JbstUserToken.fixedMagicLink()
        );

        // Assert
        JbstSleep.sleep(5, TimeUnit.SECONDS);
    }

    @Disabled
    @Test
    void executeEmailConfirmation() {
        // Act
        this.componentUnderTest.executeEmailConfirmation(
                JbstUserToken.fixedEmailConfirmation()
        );

        // Assert
        JbstSleep.sleep(5, TimeUnit.SECONDS);
    }

    @Disabled
    @Test
    void executePasswordReset() {
        // Act
        this.componentUnderTest.executePasswordReset(
                JbstUserToken.fixedPasswordReset()
        );

        // Assert
        JbstSleep.sleep(5, TimeUnit.SECONDS);
    }

    @Disabled
    @Test
    void executeAuthenticationLogin() {
        // Act
        this.componentUnderTest.executeAccountAccessed(
                JbstFunctionAccountAccessed.fixed(
                        JbstAccountAccessMethod.USERNAME_PASSWORD
                )
        );

        // Assert
        JbstSleep.sleep(5, TimeUnit.SECONDS);
    }

    @Disabled
    @Test
    void executeSessionRefreshed() {
        // Act
        this.componentUnderTest.executeAccountAccessed(
                JbstFunctionAccountAccessed.fixed(
                        JbstAccountAccessMethod.SESSION_TOKEN
                )
        );

        // Assert
        JbstSleep.sleep(5, TimeUnit.SECONDS);
    }
}
