package jbst.iam.server.configurations;

import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import jbst.foundation.utils.EnvironmentUtils;
import jbst.iam.handlers.exceptions.ResourceExceptionHandler;
import jbst.iam.server.base.services.UsersService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.Mockito.mock;

@Configuration
@ComponentScan({
        "jbst.iam.server.base.resources"
})
@EnableWebMvc
public class TestConfigurationResources {

    // =================================================================================================================
    // Exceptions
    // =================================================================================================================
    @Bean
    ResourceExceptionHandler resourceExceptionHandler() {
        return new ResourceExceptionHandler(this.incidentPublisher());
    }

    @Bean
    IncidentPublisher incidentPublisher() {
        return mock(IncidentPublisher.class);
    }

    // =================================================================================================================
    // Services
    // =================================================================================================================
    @Bean
    UsersService usersService() {
        return mock(UsersService.class);
    }

    // =================================================================================================================
    // Utils
    // =================================================================================================================
    @Bean
    EnvironmentUtils environmentUtils() {
        return mock(EnvironmentUtils.class);
    }
}
