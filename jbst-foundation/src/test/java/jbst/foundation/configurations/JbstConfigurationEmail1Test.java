package jbst.foundation.configurations;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.services.JbstEmailService;
import jbst.foundation.services.emails.JbstEmailServiceEnabled;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.lang.reflect.Method;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("SpringBootApplicationProperties")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "jbst.async.thread-name-prefix=tps1-async",
                "jbst.async.threads-core-pool-percentage=25",
                "jbst.async.threads-max-pool-percentage=50",
                "jbst.emails.enabled=true",
                "jbst.emails.host=smtp.gmail.com",
                "jbst.emails.port=587",
                "jbst.emails.from=jbst",
                "jbst.emails.username=jbst",
                "jbst.emails.password=jbst"
        }
)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstConfigurationEmail1Test {

    @Configuration
    @Import({
            JbstConfigurationEmail.class
    })
    static class ContextConfiguration {

    }

    private final JbstProperties jbstProperties;

    private final JbstConfigurationEmail componentUnderTest;

    @Test
    void beansTests() {
        // Act
        var methods = Stream.of(this.componentUnderTest.getClass().getMethods())
                .map(Method::getName)
                .collect(Collectors.toList());

        // Assert
        assertThat(methods)
                .contains("javaMailSender")
                .contains("springTemplateEngine")
                .contains("htmlTemplateResolver")
                .contains("emailService")
                .contains("emailServiceSlf4j")
                .hasSize(19);
    }

    @Test
    void javaMailSenderTest() {
        // Act
        var javaMailSender = (JavaMailSenderImpl) this.componentUnderTest.javaMailSender();

        // Assert
        var emails = this.jbstProperties.getEmails();
        assertThat(javaMailSender.getHost()).isEqualTo(emails.getHost());
        assertThat(javaMailSender.getPort()).isEqualTo(emails.getPort());
        assertThat(javaMailSender.getUsername()).isEqualTo(emails.getUsername().value());
        assertThat(javaMailSender.getPassword()).isEqualTo(emails.getPassword().value());
        assertThat(javaMailSender.getJavaMailProperties()).hasSize(4);
        assertThat(javaMailSender.getJavaMailProperties()).containsEntry("mail.transport.protocol", "smtp");
        assertThat(javaMailSender.getJavaMailProperties()).containsEntry("mail.smtp.auth", "true");
        assertThat(javaMailSender.getJavaMailProperties()).containsEntry("mail.smtp.starttls.enable", "true");
        assertThat(javaMailSender.getJavaMailProperties()).containsEntry("mail.debug", "false");
    }

    @Test
    void emailServiceTest() {
        // Act
        var incidentClientDefinition = this.componentUnderTest.emailService();

        // Assert
        assertThat(incidentClientDefinition.getClass()).isNotEqualTo(JbstEmailService.class);
        assertThat(incidentClientDefinition.getClass()).isEqualTo(JbstEmailServiceEnabled.class);
    }

    @Test
    void emailServiceSlf4jTest() {
        // Act + Assert
        assertThatThrownBy(this.componentUnderTest::emailServiceSlf4j)
                .isInstanceOf(NoSuchBeanDefinitionException.class)
                .hasMessage("No bean named 'emailServiceSlf4j' available");
    }
}
