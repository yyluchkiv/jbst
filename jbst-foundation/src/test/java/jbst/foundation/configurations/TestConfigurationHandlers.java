package jbst.foundation.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.events.publishers.JbstIncidentsPublisher;
import jbst.foundation.utils.JbstHttpUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
@ComponentScan({
        "jbst.foundation.handlers"
})
public class TestConfigurationHandlers {

    @Bean
    JbstEventsPublisher securityJwtEventsPublisher() {
        return mock(JbstEventsPublisher.class);
    }

    @Bean
    JbstIncidentsPublisher securityJwtIncidentsPublisher() {
        return mock(JbstIncidentsPublisher.class);
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
