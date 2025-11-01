package jbst.server.iam.configurations;

import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.handlers.JbstResourceExceptionHandler;
import jbst.foundation.utils.JbstEnvUtils;
import jbst.server.iam.base.services.UsersService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.Mockito.mock;

@Configuration
@ComponentScan({
        "jbst.server.iam.base.resources"
})
@EnableWebMvc
public class TestConfigurationResources {

    // =================================================================================================================
    // Exceptions
    // =================================================================================================================
    @Bean
    JbstResourceExceptionHandler resourceExceptionHandler() {
        return new JbstResourceExceptionHandler(this.incidentsPublisher());
    }

    @Bean
    JbstIncidentsPublisher incidentsPublisher() {
        return mock(JbstIncidentsPublisher.class);
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
    JbstEnvUtils envUtils() {
        return mock(JbstEnvUtils.class);
    }
}
