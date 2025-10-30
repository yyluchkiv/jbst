package jbst.foundation.events.subscribers;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.RequestUserRegistration0;
import jbst.foundation.domain.events.*;
import jbst.foundation.domain.functions.FunctionAccountAccessed;
import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.domain.http.requests.UserAgentHeader;
import jbst.foundation.domain.http.requests.UserRequestMetadata;
import jbst.foundation.events.publishers.JbstIncidentsPublisher;
import jbst.foundation.incidents.domain.authetication.IncidentAuthenticationLogin;
import jbst.foundation.incidents.domain.authetication.IncidentAuthenticationLoginFailureUsernameMaskedPassword;
import jbst.foundation.incidents.domain.authetication.IncidentAuthenticationLoginFailureUsernamePassword;
import jbst.foundation.incidents.domain.session.IncidentSessionRefreshed;
import jbst.foundation.services.JbstUsersSessionsService;
import jbst.foundation.services.JbstUsersTokensService;
import jbst.foundation.services.base.JbstUsersEmailsService;
import jbst.foundation.utils.JbstGeoUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.enums.AccountAccessMethod.SESSION_TOKEN;
import static jbst.foundation.domain.enums.AccountAccessMethod.USERNAME_PASSWORD;
import static jbst.foundation.utilities.random.EntityUtility.entity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstEventsSubscriberTest {

    private static Stream<Arguments> exceptionalExecutionParams() {
        return Stream.of(
                Arguments.of(new IllegalArgumentException()),
                Arguments.of((Object) null)
        );
    }

    private static Stream<Arguments> eventSessionUserRequestMetadataAddLoginTest() {
        return Stream.of(
                Arguments.of(
                        new EventSessionUserRequestMetadataAdd(
                                Username.random(),
                                Email.random(),
                                entity(JbstUserSession.class),
                                IPAddress.random(),
                                mock(UserAgentHeader.class),
                                USERNAME_PASSWORD
                        ),
                        null
                ),
                Arguments.of(
                        new EventSessionUserRequestMetadataAdd(
                                Username.random(),
                                null,
                                entity(JbstUserSession.class),
                                IPAddress.random(),
                                mock(UserAgentHeader.class),
                                USERNAME_PASSWORD
                        ),
                        null
                ),
                Arguments.of(
                        new EventSessionUserRequestMetadataAdd(
                                Username.random(),
                                null,
                                entity(JbstUserSession.class),
                                IPAddress.random(),
                                mock(UserAgentHeader.class),
                                USERNAME_PASSWORD
                        ),
                        new RuntimeException("Unexpected error occurred")
                )
        );
    }

    private static Stream<Arguments> eventSessionUserRequestMetadataAddRefreshTest() {
        return Stream.of(
                Arguments.of(
                        new EventSessionUserRequestMetadataAdd(
                                Username.random(),
                                Email.random(),
                                entity(JbstUserSession.class),
                                IPAddress.random(),
                                mock(UserAgentHeader.class),
                                SESSION_TOKEN
                        ),
                        null
                ),
                Arguments.of(
                        new EventSessionUserRequestMetadataAdd(
                                Username.random(),
                                null,
                                entity(JbstUserSession.class),
                                IPAddress.random(),
                                mock(UserAgentHeader.class),
                                SESSION_TOKEN
                        ),
                        null
                ),
                Arguments.of(
                        new EventSessionUserRequestMetadataAdd(
                                Username.random(),
                                null,
                                entity(JbstUserSession.class),
                                IPAddress.random(),
                                mock(UserAgentHeader.class),
                                SESSION_TOKEN
                        ),
                        new RuntimeException("Unexpected error occurred")
                )
        );
    }

    public static Stream<Arguments> onSessionUserRequestMetadataRenewTest() {
        return Stream.of(
                Arguments.of((RuntimeException) null),
                Arguments.of(new RuntimeException("Unexpected error occurred"))
        );
    }

    @Configuration
    static class ContextConfiguration {
        @Bean
        JbstIncidentsPublisher securityJwtIncidentPublisher() {
            return mock(JbstIncidentsPublisher.class);
        }

        @Bean
        JbstUsersTokensService baseUsersTokensService() {
            return mock(JbstUsersTokensService.class);
        }

        @Bean
        JbstUsersEmailsService userEmailService() {
            return mock(JbstUsersEmailsService.class);
        }

        @Bean
        JbstUsersSessionsService usersSessionsService() {
            return mock(JbstUsersSessionsService.class);
        }

        @Bean
        JbstGeoUtils geoUtils() {
            return mock(JbstGeoUtils.class);
        }

        @Bean
        JbstEventsSubscriber eventsSubscriber() {
            return new JbstEventsSubscriber(
                    this.securityJwtIncidentPublisher(),
                    this.baseUsersTokensService(),
                    this.userEmailService(),
                    this.usersSessionsService(),
                    this.geoUtils()
            );
        }
    }

    // Publishers
    private final JbstIncidentsPublisher incidentsPublisher;
    // Services
    private final JbstUsersTokensService usersTokensService;
    private final JbstUsersEmailsService usersEmailsService;
    private final JbstUsersSessionsService usersSessionsService;
    // Utils
    private final JbstGeoUtils geoUtils;

    private final JbstEventsSubscriber componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.incidentsPublisher,
                this.usersTokensService,
                this.usersEmailsService,
                this.usersSessionsService,
                this.geoUtils
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.incidentsPublisher,
                this.usersTokensService,
                this.usersEmailsService,
                this.usersSessionsService,
                this.geoUtils
        );
    }

    @Test
    void onAuthenticationLoginMagicLinkFailureTest() {
        // Arrange
        var event = entity(EventAuthenticationMagicLinkFailure.class);

        // Act
        this.componentUnderTest.onAuthenticationLoginMagicLinkFailure(event);

        // Assert
        assertThat(event).isNotNull();
    }

    @Test
    void onAuthenticationLoginTest() {
        // Arrange
        var event = entity(EventAuthenticationLogin.class);

        // Act
        this.componentUnderTest.onAuthenticationLogin(event);

        // Assert
        assertThat(event).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("exceptionalExecutionParams")
    void onAuthenticationLoginFailureTest(RuntimeException ex) {
        // Arrange
        var event = EventAuthenticationLoginFailure.hardcoded();
        when(this.geoUtils.getUserRequestMetadataProcessed(event.ipAddress(), event.userAgentHeader())).thenReturn(UserRequestMetadata.valid());
        if (nonNull(ex)) {
            doThrow(ex).when(this.incidentsPublisher).publishAuthenticationLoginFailureUsernameMaskedPassword(any());
        }

        // Act
        this.componentUnderTest.onAuthenticationLoginFailure(event);

        // Assert
        verify(this.geoUtils).getUserRequestMetadataProcessed(event.ipAddress(), event.userAgentHeader());
        verify(this.incidentsPublisher).publishAuthenticationLoginFailureUsernamePassword(
                new IncidentAuthenticationLoginFailureUsernamePassword(
                        new UsernamePasswordCredentials(
                                event.username(),
                                event.password()
                        ),
                        UserRequestMetadata.valid()
                )
        );
        verify(this.incidentsPublisher).publishAuthenticationLoginFailureUsernameMaskedPassword(
                new IncidentAuthenticationLoginFailureUsernameMaskedPassword(
                        UsernamePasswordCredentials.mask5(
                                event.username(),
                                event.password()
                        ),
                        UserRequestMetadata.valid()
                )
        );
        verify(this.incidentsPublisher, nonNull(ex) ? times(1) : times(0)).publishThrowable(ex);
    }

    @Test
    void onAuthenticationLogoutTest() {
        // Arrange
        var event = entity(EventAuthenticationLogout.class);

        // Act
        this.componentUnderTest.onAuthenticationLogout(event);

        // Assert
        assertThat(event).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("exceptionalExecutionParams")
    void onRegistration0Test(RuntimeException ex) {
        // Arrange
        var requestUserRegistration0 = RequestUserRegistration0.hardcoded();
        var event = new EventRegistration0(requestUserRegistration0);
        var userToken = JbstUserToken.hardcodedEmailConfirmation();
        when(this.usersTokensService.saveAs(requestUserRegistration0.asRequestUserToken())).thenReturn(userToken);
        if (nonNull(ex)) {
            doThrow(ex).when(this.usersEmailsService).executeEmailConfirmation(userToken);
        }

        // Act
        this.componentUnderTest.onRegistration0(event);

        // Assert
        assertThat(event).isNotNull();
        verify(this.usersTokensService).saveAs(requestUserRegistration0.asRequestUserToken());
        verify(this.usersEmailsService).executeEmailConfirmation(userToken);
        verify(this.incidentsPublisher, nonNull(ex) ? times(1) : times(0)).publishThrowable(ex);
    }

    @Test
    void onRegistrationMagicLinkTest() {
        // Arrange
        var event = entity(EventRegistrationMagicLink.class);

        // Act
        this.componentUnderTest.onRegistrationMagicLink(event);

        // Assert
        assertThat(event).isNotNull();
    }

    @Test
    void onRegistration0FailureTest() {
        // Arrange
        var event = entity(EventRegistration0Failure.class);

        // Act
        this.componentUnderTest.onRegistration0Failure(event);

        // Assert
        assertThat(event).isNotNull();
    }

    @Test
    void onRegistration1Test() {
        // Arrange
        var event = entity(EventRegistration1.class);

        // Act
        this.componentUnderTest.onRegistration1(event);

        // Assert
        assertThat(event).isNotNull();
    }

    @Test
    void onRegistration1FailureTest() {
        // Arrange
        var event = entity(EventRegistration1Failure.class);

        // Act
        this.componentUnderTest.onRegistration1Failure(event);

        // Assert
        assertThat(event).isNotNull();
    }

    @Test
    void onSessionRefreshedTest() {
        // Arrange
        var event = entity(EventSessionRefreshed.class);

        // Act
        this.componentUnderTest.onSessionRefreshed(event);

        // Assert
        assertThat(event).isNotNull();
    }

    @Test
    void onSessionExpiredTest() {
        // Arrange
        var event = entity(EventSessionExpired.class);

        // Act
        this.componentUnderTest.onSessionExpired(event);

        // Assert
        assertThat(event).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("eventSessionUserRequestMetadataAddLoginTest")
    void onSessionUserRequestMetadataAddIsAuthenticationLoginEndpointTest(
            EventSessionUserRequestMetadataAdd event,
            RuntimeException ex
    ) {
        // Arrange
        when(this.usersSessionsService.saveUserRequestMetadata(event)).thenReturn(event.session());
        if (nonNull(ex)) {
            doThrow(ex).when(this.incidentsPublisher).publishAuthenticationLogin(any());
        }

        // Act
        this.componentUnderTest.onSessionUserRequestMetadataAdd(event);

        // Assert
        verify(this.usersSessionsService).saveUserRequestMetadata(event);
        if (nonNull(event.email())) {
            verify(this.usersEmailsService).executeAccountAccessed(new FunctionAccountAccessed(event.username(), event.email(), event.session().metadata(), USERNAME_PASSWORD));
        } else {
            verifyNoInteractions(this.usersEmailsService);
        }
        verify(this.incidentsPublisher).publishAuthenticationLogin(new IncidentAuthenticationLogin(event.username(), event.session().metadata()));
        verify(this.incidentsPublisher, nonNull(ex) ? times(1) : times(0)).publishThrowable(ex);
    }

    @ParameterizedTest
    @MethodSource("eventSessionUserRequestMetadataAddRefreshTest")
    void onSessionUserRequestMetadataAddIsAuthenticationRefreshTokenEndpointTest(
            EventSessionUserRequestMetadataAdd event,
            RuntimeException ex
    ) {
        // Arrange
        when(this.usersSessionsService.saveUserRequestMetadata(event)).thenReturn(event.session());
        if (nonNull(ex)) {
            doThrow(ex).when(this.incidentsPublisher).publishSessionRefreshed(any());
        }

        // Act
        this.componentUnderTest.onSessionUserRequestMetadataAdd(event);

        // Assert
        verify(this.usersSessionsService).saveUserRequestMetadata(event);
        if (nonNull(event.email())) {
            verify(this.usersEmailsService).executeAccountAccessed(new FunctionAccountAccessed(event.username(), event.email(), event.session().metadata(), SESSION_TOKEN));
        } else {
            verifyNoInteractions(this.usersEmailsService);
        }
        verify(this.incidentsPublisher).publishSessionRefreshed(new IncidentSessionRefreshed(event.username(), event.session().metadata()));
        verify(this.incidentsPublisher, nonNull(ex) ? times(1) : times(0)).publishThrowable(ex);
    }

    @ParameterizedTest
    @MethodSource("onSessionUserRequestMetadataRenewTest")
    void onSessionUserRequestMetadataRenewTest(RuntimeException ex) {
        // Arrange
        var event = entity(EventSessionUserRequestMetadataRenew.class);
        if (nonNull(ex)) {
            doThrow(ex).when(this.usersSessionsService).saveUserRequestMetadata(event);
        }

        // Act
        this.componentUnderTest.onSessionUserRequestMetadataRenew(event);

        // Assert
        verify(this.usersSessionsService).saveUserRequestMetadata(event);
        verify(this.incidentsPublisher, nonNull(ex) ? times(1) : times(0)).publishThrowable(ex);
    }
}
