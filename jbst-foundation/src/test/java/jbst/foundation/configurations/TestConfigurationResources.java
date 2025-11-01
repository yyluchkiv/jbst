package jbst.foundation.configurations;

import jbst.foundation.assistants.current.CurrentSessionAssistant;
import jbst.foundation.assistants.userdetails.JbstJwtUserDetailsService;
import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.extension.JbstExtensionService;
import jbst.foundation.handlers.JbstResourceExceptionHandler;
import jbst.foundation.repositories.JbstUsersRepository;
import jbst.foundation.repositories.JbstUsersTokensRepository;
import jbst.foundation.resources.hardware.JbstHardwareMonitoringStore;
import jbst.foundation.services.*;
import jbst.foundation.services.base.JbstAuthenticationService;
import jbst.foundation.services.base.JbstRateLimitsService;
import jbst.foundation.services.base.JbstTokensService;
import jbst.foundation.services.base.JbstUsersEmailsService;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.settings.JbstSettingsService;
import jbst.foundation.tokens.facade.JbstTokensProvider;
import jbst.foundation.utils.JbstEnvUtils;
import jbst.foundation.validators.*;
import jbst.foundation.validators.base.JbstAuthenticationValidator;
import jbst.foundation.websockets.JbstWebsocketsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.Mockito.mock;

@Configuration
@ComponentScan({
        "jbst.foundation.resources",
})
@EnableWebMvc
public class TestConfigurationResources {

    // =================================================================================================================
    // Settings
    // =================================================================================================================
    @Bean
    JbstSettingsService jbstSettingsService() {
        return mock(JbstSettingsService.class);
    }

    // =================================================================================================================
    // Exceptions
    // =================================================================================================================
    @Bean
    JbstResourceExceptionHandler resourceExceptionHandler() {
        return new JbstResourceExceptionHandler(this.incidentsPublishers());
    }

    // =================================================================================================================
    // Authentication
    // =================================================================================================================
    @Bean
    JbstAuthenticationService authenticationService() {
        return new JbstAuthenticationService(
                this.authenticationManager(),
                this.jwtUserDetailsService(),
                this.sessionRegistry(),
                this.usersService(),
                this.usersSessionsService(),
                this.tokenService(),
                this.usersTokensRepository(),
                this.tokensProvider(),
                this.securityUtils(),
                this.securityJwtEventsPublisher()
        );
    }

    @Bean
    AuthenticationManager authenticationManager() {
        return mock(AuthenticationManager.class);
    }

    // =================================================================================================================
    // Sessions
    // =================================================================================================================
    @Bean
    JbstSessionRegistry sessionRegistry() {
        return mock(JbstSessionRegistry.class);
    }

    // =================================================================================================================
    // Services
    // =================================================================================================================
    @Bean
    JbstExtensionService extensionService() {
        return mock(JbstExtensionService.class);
    }

    @Bean
    JbstRateLimitsService rateLimitsService() {
        return new JbstRateLimitsService();
    }

    @Bean
    JbstUsersService usersService() {
        return mock(JbstUsersService.class);
    }

    @Bean
    JbstUsersTokensService baseUsersTokensService() {
        return mock(JbstUsersTokensService.class);
    }

    @Bean
    JbstUsersEmailsService usersEmailsService() {
        return mock(JbstUsersEmailsService.class);
    }

    @Bean
    JbstInvitationsService baseInvitationsService() {
        return mock(JbstInvitationsService.class);
    }

    @Bean
    JbstRegistrationService baseRegistrationService() {
        return mock(JbstRegistrationService.class);
    }

    @Bean
    JbstSuperadminService baseSuperadminService() {
        return mock(JbstSuperadminService.class);
    }

    @Bean
    JbstTokensService tokenService() {
        return mock(JbstTokensService.class);
    }

    @Bean
    JbstUsersSessionsService usersSessionsService() {
        return mock(JbstUsersSessionsService.class);
    }

    // =================================================================================================================
    // Assistants
    // =================================================================================================================
    @Bean
    CurrentSessionAssistant currentSessionAssistant() {
        return mock(CurrentSessionAssistant.class);
    }

    @Bean
    JbstJwtUserDetailsService jwtUserDetailsService() {
        return mock(JbstJwtUserDetailsService.class);
    }

    // =================================================================================================================
    // Publishers
    // =================================================================================================================
    @Bean
    JbstEventsPublisher securityJwtEventsPublisher() {
        return mock(JbstEventsPublisher.class);
    }

    @Bean
    JbstIncidentsPublisher incidentsPublishers() {
        return mock(JbstIncidentsPublisher.class);
    }

    // =================================================================================================================
    // Tokens
    // =================================================================================================================
    @Bean
    JbstTokensProvider tokensProvider() {
        return mock(JbstTokensProvider.class);
    }

    // =================================================================================================================
    // Passwords
    // =================================================================================================================
    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return mock(BCryptPasswordEncoder.class);
    }

    // =================================================================================================================
    // Utils
    // =================================================================================================================
    @Bean
    JbstSecurityUtils securityUtils() {
        return mock(JbstSecurityUtils.class);
    }

    @Bean
    JbstEnvUtils envUtils() {
        return mock(JbstEnvUtils.class);
    }

    // =================================================================================================================
    // Validators
    // =================================================================================================================
    @Bean
    JbstAuthenticationValidator authenticationRequestsValidator() {
        return mock(JbstAuthenticationValidator.class);
    }

    @Bean
    JbstInvitationsValidator invitationsRequestsValidator() {
        return mock(JbstInvitationsValidator.class);
    }

    @Bean
    JbstRegistrationValidator registrationRequestsValidator() {
        return mock(JbstRegistrationValidator.class);
    }

    @Bean
    JbstUsersSessionsValidator sessionsRequestsValidator() {
        return mock(JbstUsersSessionsValidator.class);
    }

    @Bean
    JbstUsersValidator userRequestsValidator() {
        return mock(JbstUsersValidator.class);
    }

    @Bean
    JbstUsersTokensValidator tokensRequestsValidator() {
        return mock(JbstUsersTokensValidator.class);
    }

    // =================================================================================================================
    // Repositories
    // =================================================================================================================
    @Bean
    JbstUsersRepository usersRepository() {
        return mock(JbstUsersRepository.class);
    }

    @Bean
    JbstUsersTokensRepository usersTokensRepository() {
        return mock(JbstUsersTokensRepository.class);
    }

    // =================================================================================================================
    // Websockets
    // =================================================================================================================
    @Bean
    JbstWebsocketsService websocketsService() {
        return mock(JbstWebsocketsService.class);
    }

    // =================================================================================================================
    // Hardware
    // =================================================================================================================
    @Bean
    JbstHardwareMonitoringStore jbstHardwareMonitoringStore() {
        return new JbstHardwareMonitoringStore(
                this.jbstSettingsService()
        );
    }
}
