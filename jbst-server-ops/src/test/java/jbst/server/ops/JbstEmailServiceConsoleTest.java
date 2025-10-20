package jbst.server.ops;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.RecipientsConfigs;
import jbst.foundation.domain.properties.configs.EmailConfigs;
import jbst.foundation.domain.properties.configs.UtilsConfigs;
import jbst.foundation.domain.emails.EmailHTML;
import jbst.foundation.services.JbstEmailService;
import jbst.foundation.services.emails.JbstEmailServiceEnabled;
import jbst.foundation.utilities.time.TimestampUtility;
import jbst.foundation.utils.JbstGeoUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstEmailServiceConsoleTest {

    @Configuration
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final ResourceLoader resourceLoader;

        @Bean
        JbstProperties jbstProperties() {
            var applicationFrameworkProperties = new JbstProperties();
            applicationFrameworkProperties.setEmailConfigs(
                    new EmailConfigs(
                            true,
                            "smtp.gmail.com",
                            587,
                            "<?> <?>",
                            Username.of("<?>"),
                            Password.of("<?>")
                    )
            );
            applicationFrameworkProperties.setUtilsConfigs(UtilsConfigs.hardcoded());
            return applicationFrameworkProperties;
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
        JbstGeoUtils geoUtils() {
            return new JbstGeoUtils(
                    this.resourceLoader,
                    this.jbstProperties()
            );
        }
    }

    private final JbstGeoUtils geoUtils;

    private final JbstEmailService componentUnderTest;

    @Test
    @Disabled
    void sendTemplate1() {
        // Arrange
        var emailHTML = new EmailHTML(
                this.getTo(),
                "Test Template #1: " + TimestampUtility.getCurrentTimestamp(),
                "tests-template1",
                Map.of(
                        "where", "Near Matosinhos Municipality, Porto, Portugal",
                        "when", "Jun 13, 2022 at 1:39 pm (WEST)",
                        "what", "Chrome on Mac OS X"
                )
        );

        // Act
        this.componentUnderTest.sendHTML(emailHTML);
    }

    @Test
    @Disabled
    void sendOpsAnyIncident() {
        // Arrange
        var ukraineFlag = this.geoUtils.getFlagEmojiByCountryCode("UA");
        var emailHTML = new EmailHTML(
                this.getTo(),
                "[OpsIncidents] Authentication Login on [server-prod@prod] — " + TimestampUtility.getCurrentTimestamp() + " — [AnyIncident]",
                "ops-any-incident",
                Map.of(
                        "members", "sherlock.holmes, mycroft.holmes",
                        "year", "2100",
                        "serverURL", "https://127.0.0.1",
                        "remoteURL", "https://127.0.0.1",
                        "username", "junit",
                        "whereCombined", ukraineFlag + " Ukraine, Lviv",
                        "what", "Chrome on Mac OS X",
                        "ipAddress", "8.8.8.8",
                        "invitationCodeOwner", "junit-owner"
                )
        );

        // Act
        this.componentUnderTest.sendHTML(emailHTML);
    }

    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    // WARNING: change to real emails
    private Set<String> getTo() {
        return new HashSet<>(RecipientsConfigs.hardcoded().getTo());
    }
}
