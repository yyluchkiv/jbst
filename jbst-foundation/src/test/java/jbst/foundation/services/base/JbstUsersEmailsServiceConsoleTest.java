package jbst.foundation.services.base;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.enums.JbstAccountAccessMethod;
import jbst.foundation.domain.functions.FunctionAccountAccessed;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.configs.JbstPropertyApp;
import jbst.foundation.domain.properties.configs.JbstPropertyEmails;
import jbst.foundation.domain.properties.configs.JbstPropertyMVC;
import jbst.foundation.domain.properties.configs.JbstPropertySecurity;
import jbst.foundation.services.JbstEmailService;
import jbst.foundation.services.emails.JbstEmailServiceEnabled;
import jbst.foundation.utilities.concurrent.SleepUtility;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
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
            jbstProperties.setApp(JbstPropertyApp.hardcoded());
            jbstProperties.setMvc(JbstPropertyMVC.hardcoded());
            jbstProperties.setSecurityJwtConfigs(JbstPropertySecurity.hardcoded());
            jbstProperties.setEmailConfigs(
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
            var emailConfigs = this.jbstProperties().getEmailConfigs();

            var mailSender = new JavaMailSenderImpl();
            mailSender.setHost(emailConfigs.getHost());
            mailSender.setPort(emailConfigs.getPort());

            mailSender.setUsername(emailConfigs.getUsername().value());
            mailSender.setPassword(emailConfigs.getPassword().value());

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
                JbstUserToken.hardcodedMagicLink()
        );

        // Assert
        SleepUtility.sleep(5, TimeUnit.SECONDS);
    }

    @Disabled
    @Test
    void executeEmailConfirmation() {
        // Act
        this.componentUnderTest.executeEmailConfirmation(
                JbstUserToken.hardcodedEmailConfirmation()
        );

        // Assert
        SleepUtility.sleep(5, TimeUnit.SECONDS);
    }

    @Disabled
    @Test
    void executePasswordReset() {
        // Act
        this.componentUnderTest.executePasswordReset(
                JbstUserToken.hardcodedPasswordReset()
        );

        // Assert
        SleepUtility.sleep(5, TimeUnit.SECONDS);
    }

    @Disabled
    @Test
    void executeAuthenticationLogin() {
        // Act
        this.componentUnderTest.executeAccountAccessed(
                FunctionAccountAccessed.hardcoded(
                        JbstAccountAccessMethod.USERNAME_PASSWORD
                )
        );

        // Assert
        SleepUtility.sleep(5, TimeUnit.SECONDS);
    }

    @Disabled
    @Test
    void executeSessionRefreshed() {
        // Act
        this.componentUnderTest.executeAccountAccessed(
                FunctionAccountAccessed.hardcoded(
                        JbstAccountAccessMethod.SESSION_TOKEN
                )
        );

        // Assert
        SleepUtility.sleep(5, TimeUnit.SECONDS);
    }
}
