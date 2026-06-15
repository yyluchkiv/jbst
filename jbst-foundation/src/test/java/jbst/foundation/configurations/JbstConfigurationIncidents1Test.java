package jbst.foundation.configurations;

import jbst.foundation.incidents.clients.JbstIncidentClientTypeLogger;
import jbst.foundation.incidents.clients.JbstIncidentClientTypeTelegram;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.reflect.Method;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("SpringBootApplicationProperties")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "spring.config.location=classpath:tests-jbst-incidents-manager-01.yml"
)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstConfigurationIncidents1Test {

    @Configuration
    @Import({
            JbstConfigurationIncidents.class
    })
    static class ContextConfiguration {

    }

    private final JbstConfigurationIncidents componentUnderTest;

    @Test
    void beansTests() {
        // Act
        var methods = Stream.of(this.componentUnderTest.getClass().getDeclaredMethods())
                .map(Method::getName)
                .collect(Collectors.toList());

        // Assert
        assertThat(methods)
                .contains("incidentsPublisher")
                .contains("incidentsSubscriber")
                .contains("rejectedExecutionHandler")
                .contains("errorHandlerPublisher")
                .contains("simpleApplicationEventMulticaster")
                .contains("incidentClient")
                .contains("incidentClient")
                .hasSizeGreaterThanOrEqualTo(22);
    }

    @Test
    void incidentClient() {
        // Act
        var incidentClient = this.componentUnderTest.incidentClient();

        // Assert
        assertThat(incidentClient.getClass()).isEqualTo(JbstIncidentClientTypeTelegram.class);
        assertThat(incidentClient.getClass()).isNotEqualTo(JbstIncidentClientTypeLogger.class);
    }

    @Test
    void incidentClientDisabled() {
        // Act + Assert
        assertThatThrownBy(this.componentUnderTest::incidentClientDisabled)
                .isInstanceOf(NoSuchBeanDefinitionException.class)
                .hasMessage("No bean named 'incidentClientDisabled' available");
    }
}
