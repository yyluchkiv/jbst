package jbst.foundation.assistants.current;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.responses.ResponseUserSessionsTable;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringWidget;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.tuples.TuplePresence;
import jbst.foundation.repositories.JbstUsersSessionsRepository;
import jbst.foundation.resources.hardware.JbstHardwareMonitoringStore;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.settings.JbstSettingsService;
import jbst.foundation.tokens.facade.JbstTokensProvider;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Set;

import static jbst.foundation.domain.random.JbstRandomEntities.entity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstCurrentSessionAssistantTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        JbstSettingsService jbstSettingsService() {
            return mock(JbstSettingsService.class);
        }

        @Bean
        JbstSessionRegistry sessionRegistry() {
            return mock(JbstSessionRegistry.class);
        }

        @Bean
        JbstUsersSessionsRepository usersSessionsRepository() {
            return mock(JbstUsersSessionsRepository.class);
        }

        @Bean
        JbstHardwareMonitoringStore jbstHardwareMonitoringStore() {
            return mock(JbstHardwareMonitoringStore.class);
        }

        @Bean
        JbstTokensProvider tokensProvider() {
            return mock(JbstTokensProvider.class);
        }

        @Bean
        JbstSecurityUtils securityUtils() {
            return mock(JbstSecurityUtils.class);
        }

        @Bean
        CurrentSessionAssistant currentSessionAssistant() {
            return new JbstCurrentSessionAssistant(
                    this.jbstSettingsService(),
                    this.sessionRegistry(),
                    this.usersSessionsRepository(),
                    this.tokensProvider(),
                    this.securityUtils(),
                    this.jbstHardwareMonitoringStore()
            );
        }
    }

    private final JbstSettingsService settingsService;
    private final JbstSessionRegistry sessionRegistry;
    private final JbstUsersSessionsRepository usersSessionsRepository;
    private final JbstTokensProvider tokensProvider;
    private final JbstSecurityUtils securityUtils;
    private final JbstHardwareMonitoringStore jbstHardwareMonitoringStore;

    private final CurrentSessionAssistant componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.settingsService,
                this.sessionRegistry,
                this.usersSessionsRepository,
                this.jbstHardwareMonitoringStore,
                this.tokensProvider,
                this.securityUtils
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.settingsService,
                this.sessionRegistry,
                this.usersSessionsRepository,
                this.jbstHardwareMonitoringStore,
                this.tokensProvider,
                this.securityUtils
        );
    }

    @Test
    void getCurrentUsernameTest() {
        // Arrange
        var expectedJwtUser = entity(JwtUser.class);
        when(this.securityUtils.getAuthenticatedUsername()).thenReturn(expectedJwtUser.getUsername());

        // Act
        var actualUsername = this.componentUnderTest.getCurrentUsername();

        // Assert
        verify(this.securityUtils).getAuthenticatedUsername();
        assertThat(actualUsername).isEqualTo(expectedJwtUser.username());
    }

    @Test
    void getCurrentJwtUserTest() {
        // Arrange
        var expectedJwtUser = entity(JwtUser.class);
        when(this.securityUtils.getAuthenticatedJwtUser()).thenReturn(expectedJwtUser);

        // Act
        var actualJwtUser = this.componentUnderTest.getCurrentJwtUser();

        // Assert
        verify(this.securityUtils).getAuthenticatedJwtUser();
        assertThat(actualJwtUser).isEqualTo(expectedJwtUser);
    }

    @Test
    void getCurrentClientUserTest() {
        // Arrange
        var user = JwtUser.hardcoded();
        when(this.securityUtils.getAuthenticatedJwtUser()).thenReturn(user);
        var hardwareMonitoringWidget = entity(HardwareMonitoringWidget.class);
        when(this.jbstHardwareMonitoringStore.getWidget()).thenReturn(hardwareMonitoringWidget);
        when(this.settingsService.isHardwareMonitoringThresholdsEnabled()).thenReturn(true);

        // Act
        var currentClientUser = this.componentUnderTest.getCurrentClientUser();

        // Assert
        verify(this.securityUtils).getAuthenticatedJwtUser();
        verify(this.jbstHardwareMonitoringStore).getWidget();
        verify(this.settingsService).isHardwareMonitoringThresholdsEnabled();
        assertThat(currentClientUser.getUsername()).isEqualTo(Username.of(user.getUsername()));
        assertThat(currentClientUser.getEmail()).isEqualTo(user.email());
        assertThat(currentClientUser.getName()).isEqualTo(user.name());
        assertThat(currentClientUser.getAttributes()).isNotNull();
        assertThat(currentClientUser.getAttributes()).hasSize(1);
        assertThat(currentClientUser.getAttributes()).containsOnlyKeys("hardware");
    }

    @Test
    void getCurrentClientUserNoAttributesNoHardwareTest() {
        // Arrange
        var user = entity(JwtUser.class);
        when(this.securityUtils.getAuthenticatedJwtUser()).thenReturn(user);
        var hardwareMonitoringWidget = entity(HardwareMonitoringWidget.class);
        when(this.jbstHardwareMonitoringStore.getWidget()).thenReturn(hardwareMonitoringWidget);
        when(this.settingsService.isHardwareMonitoringThresholdsEnabled()).thenReturn(false);

        // Act
        var currentClientUser = this.componentUnderTest.getCurrentClientUser();

        // Assert
        verify(this.securityUtils).getAuthenticatedJwtUser();
        verify(this.settingsService).isHardwareMonitoringThresholdsEnabled();
        assertThat(currentClientUser.getUsername()).isEqualTo(Username.of(user.getUsername()));
        assertThat(currentClientUser.getEmail()).isEqualTo(user.email());
        assertThat(currentClientUser.getName()).isEqualTo(user.name());
        assertThat(currentClientUser.getAttributes()).isNotNull();
        assertThat(currentClientUser.getAttributes()).isEmpty();
    }

    @Test
    void getCurrentUserSessionTest() throws JbstExceptions.AccessTokenNotFound {
        // Arrange
        var session = entity(JbstUserSession.class);
        var request = mock(HttpServletRequest.class);
        var requestAccessToken = RequestAccessToken.random();
        var accessToken = JwtAccessToken.of(requestAccessToken.value());
        when(this.tokensProvider.readRequestAccessToken(request)).thenReturn(requestAccessToken);
        when(this.usersSessionsRepository.isPresent(accessToken)).thenReturn(TuplePresence.present(session));

        // Act
        var actual = this.componentUnderTest.getCurrentUserSession(request);

        // Assert
        verify(this.tokensProvider).readRequestAccessToken(request);
        verify(this.usersSessionsRepository).isPresent(accessToken);
        assertThat(actual).isEqualTo(session);
    }

    @Test
    void getCurrentUserDbSessionsTableTest() {
        // Arrange
        var username = Username.random();
        var requestAccessToken = RequestAccessToken.random();
        var sessionsTable = entity(ResponseUserSessionsTable.class);
        when(this.securityUtils.getAuthenticatedUsername()).thenReturn(username.value());
        when(this.sessionRegistry.getSessionsTable(username, requestAccessToken)).thenReturn(sessionsTable);

        // Act
        var actual = this.componentUnderTest.getCurrentUserDbSessionsTable(requestAccessToken);

        // Assert
        verify(this.securityUtils).getAuthenticatedUsername();
        verify(this.sessionRegistry).cleanByExpiredRefreshTokens(Set.of(username));
        verify(this.sessionRegistry).getSessionsTable(username, requestAccessToken);
        assertThat(actual).isEqualTo(sessionsTable);
    }
}
