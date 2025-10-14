package jbst.iam.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import jbst.foundation.utils.JbstHttpUtils;
import jbst.foundation.events.publishers.events.SecurityJwtEventsPublisher;
import jbst.foundation.events.publishers.incidents.SecurityJwtIncidentsPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
@ComponentScan({
        "jbst.iam.handlers"
})
public class TestConfigurationHandlers {

    @Bean
    IncidentPublisher incidentPublisher() {
        return mock(IncidentPublisher.class);
    }

    @Bean
    SecurityJwtEventsPublisher securityJwtEventsPublisher() {
        return mock(SecurityJwtEventsPublisher.class);
    }

    @Bean
    SecurityJwtIncidentsPublisher securityJwtIncidentsPublisher() {
        return mock(SecurityJwtIncidentsPublisher.class);
    }

    @Bean
    JbstHttpUtils httpUtils() {
        return mock(JbstHttpUtils.class);
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
