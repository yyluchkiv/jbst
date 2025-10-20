package jbst.foundation.configurations;

import jbst.foundation.assistants.current.CurrentSessionAssistant;
import jbst.foundation.assistants.userdetails.JbstJwtUserDetailsService;
import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.events.publishers.events.SecurityJwtEventsPublisher;
import jbst.foundation.events.publishers.incidents.SecurityJwtIncidentsPublisher;
import jbst.foundation.handlers.JbstResourceExceptionHandler;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import jbst.foundation.repositories.JbstUsersRepository;
import jbst.foundation.repositories.JbstUsersTokensRepository;
import jbst.foundation.resources.hardware.JbstHardwareMonitoringStore;
import jbst.foundation.services.*;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.settings.JbstSettingsService;
import jbst.foundation.tokens.facade.TokensProvider;
import jbst.foundation.utils.JbstEnvUtils;
import jbst.foundation.validators.*;
import jbst.foundation.websockets.WebsocketsService;
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
        return new JbstResourceExceptionHandler(this.incidentPublisher());
    }

    // =================================================================================================================
    // Authentication
    // =================================================================================================================
    @Bean
    AuthenticationService authenticationService() {
        return new AuthenticationService(
                this.authenticationManager(),
                this.currentSessionAssistant(),
                this.jwtUserDetailsService(),
                this.sessionRegistry(),
                this.baseUsersService(),
                this.baseUsersSessionsService(),
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
    JbstSynchronousExtensionService synchronousExtensionService() {
        return mock(JbstSynchronousExtensionService.class);
    }

    @Bean
    RateLimitsService rateLimitsService() {
        return new RateLimitsService();
    }

    @Bean
    BaseUsersService baseUsersService() {
        return mock(BaseUsersService.class);
    }

    @Bean
    BaseUsersTokensService baseUsersTokensService() {
        return mock(BaseUsersTokensService.class);
    }

    @Bean
    UsersEmailsService usersEmailsService() {
        return mock(UsersEmailsService.class);
    }

    @Bean
    BaseInvitationsService baseInvitationsService() {
        return mock(BaseInvitationsService.class);
    }

    @Bean
    BaseRegistrationService baseRegistrationService() {
        return mock(BaseRegistrationService.class);
    }

    @Bean
    BaseSuperadminService baseSuperadminService() {
        return mock(BaseSuperadminService.class);
    }

    @Bean
    TokensService tokenService() {
        return mock(TokensService.class);
    }

    @Bean
    BaseUsersSessionsService baseUsersSessionsService() {
        return mock(BaseUsersSessionsService.class);
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
    SecurityJwtEventsPublisher securityJwtEventsPublisher() {
        return mock(SecurityJwtEventsPublisher.class);
    }

    @Bean
    SecurityJwtIncidentsPublisher securityJwtIncidentsPublisher() {
        return mock(SecurityJwtIncidentsPublisher.class);
    }

    @Bean
    IncidentPublisher incidentPublisher() {
        return mock(IncidentPublisher.class);
    }

    // =================================================================================================================
    // Tokens
    // =================================================================================================================
    @Bean
    TokensProvider tokensProvider() {
        return mock(TokensProvider.class);
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
    BaseAuthenticationRequestsValidator authenticationRequestsValidator() {
        return mock(BaseAuthenticationRequestsValidator.class);
    }

    @Bean
    BaseInvitationsRequestsValidator invitationsRequestsValidator() {
        return mock(BaseInvitationsRequestsValidator.class);
    }

    @Bean
    BaseRegistrationRequestsValidator registrationRequestsValidator() {
        return mock(BaseRegistrationRequestsValidator.class);
    }

    @Bean
    BaseUsersSessionsRequestsValidator sessionsRequestsValidator() {
        return mock(BaseUsersSessionsRequestsValidator.class);
    }

    @Bean
    BaseUsersValidator userRequestsValidator() {
        return mock(BaseUsersValidator.class);
    }

    @Bean
    BaseUsersTokensRequestsValidator tokensRequestsValidator() {
        return mock(BaseUsersTokensRequestsValidator.class);
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
    WebsocketsService websocketsService() {
        return mock(WebsocketsService.class);
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
