package jbst.foundation.services.base;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.enums.AccountAccessMethod;
import jbst.foundation.domain.functions.FunctionAccountAccessed;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.configs.EmailConfigs;
import jbst.foundation.domain.properties.configs.MvcConfigs;
import jbst.foundation.domain.properties.configs.SecurityJwtConfigs;
import jbst.foundation.domain.properties.configs.ServerConfigs;
import jbst.foundation.services.UsersEmailsService;
import jbst.foundation.services.emails.services.EmailService;
import jbst.foundation.services.emails.services.impl.EmailServiceImpl;
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
class BaseUsersEmailsServiceConsoleTest {

    @Configuration
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final ResourceLoader resourceLoader;

        @Bean
        JbstProperties jbstProperties() {
            var jbstProperties = new JbstProperties();
            jbstProperties.setServerConfigs(ServerConfigs.hardcoded());
            jbstProperties.setMvcConfigs(MvcConfigs.hardcoded());
            jbstProperties.setSecurityJwtConfigs(SecurityJwtConfigs.hardcoded());
            jbstProperties.setEmailConfigs(
                    new EmailConfigs(
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
        EmailService emailService() {
            return new EmailServiceImpl(
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
        public UsersEmailsService userEmailService() {
            return new BaseUsersEmailsService(
                    this.resourceLoader,
                    this.emailService(),
                    this.jbstProperties(),
                    this.serverProperties()
            );
        }
    }

    private final UsersEmailsService componentUnderTest;

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
        this.componentUnderTest.executeAuthenticationLogin(
                FunctionAccountAccessed.hardcoded(
                        AccountAccessMethod.USERNAME_PASSWORD
                )
        );

        // Assert
        SleepUtility.sleep(5, TimeUnit.SECONDS);
    }

    @Disabled
    @Test
    void executeSessionRefreshed() {
        // Act
        this.componentUnderTest.executeAuthenticationLogin(
                FunctionAccountAccessed.hardcoded(
                        AccountAccessMethod.SECURITY_TOKEN
                )
        );

        // Assert
        SleepUtility.sleep(5, TimeUnit.SECONDS);
    }
}
