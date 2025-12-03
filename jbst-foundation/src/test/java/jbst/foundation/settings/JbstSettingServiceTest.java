package jbst.foundation.settings;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.JbstPropertyUserOnInit;
import jbst.foundation.domain.properties.configs.JbstPropertySecurity;
import jbst.foundation.repositories.JbstInvitationsRepository;
import jbst.foundation.repositories.JbstSettingsRepository;
import jbst.foundation.repositories.JbstUsersRepository;
import jbst.foundation.tests.stubbers.AbstractMockService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static jbst.foundation.domain.random.JbstRandom.randomLongGreaterThanZero;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
class JbstSettingServiceTest {

    private static Stream<Arguments> usersPresenceTest() {
        return Stream.of(
                Arguments.of(0),
                Arguments.of(randomLongGreaterThanZero())
        );
    }

    @Configuration
    static class ContextConfiguration {

        @Bean
        JbstProperties jbstProperties() {
            return mock(JbstProperties.class);
        }

        @Bean
        JbstSettingsService settingsService() {
            var settingsService = mock(JbstSettingsService.class);
            setField(settingsService, "jbstProperties", this.jbstProperties());
            return settingsService;
        }

        @Bean
        JbstSettingsRepository settingsRepository() {
            return mock(JbstSettingsRepository.class);
        }

        @Bean
        JbstInvitationsRepository invitationsRepository() {
            return mock(JbstInvitationsRepository.class);
        }

        @Bean
        JbstUsersRepository usersRepository() {
            return mock(JbstUsersRepository.class);
        }

        @Bean
        AbstractMockService abstractMockService() {
            return mock(AbstractMockService.class);
        }

        @Bean("abstractSettingsService")
        JbstSettingsService abstractSettingsService() {
            return new JbstSettingsService(
                    this.settingsRepository(),
                    this.invitationsRepository(),
                    this.usersRepository(),
                    this.jbstProperties()
            ) {
                @Override
                public long initUsers(List<JbstPropertyUserOnInit> usersOnInit) {
                    abstractMockService().executeInheritedMethod();
                    return 0;
                }

                @Override
                public void initInvitations(JbstPropertyUserOnInit userOnInit, Set<SimpleGrantedAuthority> authorities) {
                    abstractMockService().executeInheritedMethod();
                }
            };
        }
    }

    // Repositories
    private final JbstInvitationsRepository invitationsRepository;
    private final JbstUsersRepository usersRepository;
    // Properties
    private final JbstProperties jbstProperties;
    // Mock
    private final AbstractMockService abstractMockService;

    private final JbstSettingsService componentUnderTest;

    @Autowired
    JbstSettingServiceTest(
            JbstInvitationsRepository invitationsRepository,
            JbstUsersRepository usersRepository,
            JbstProperties jbstProperties,
            AbstractMockService abstractMockService,
            @Qualifier("abstractSettingsService") JbstSettingsService componentUnderTest
    ) {
        this.invitationsRepository = invitationsRepository;
        this.usersRepository = usersRepository;
        this.jbstProperties = jbstProperties;
        this.abstractMockService = abstractMockService;
        this.componentUnderTest = componentUnderTest;
    }

    @BeforeEach
    void beforeEach() {
        reset(
                this.invitationsRepository,
                this.usersRepository,
                this.jbstProperties,
                this.abstractMockService
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.invitationsRepository,
                this.usersRepository,
                this.jbstProperties,
                this.abstractMockService
        );
    }

    @ParameterizedTest
    @MethodSource("usersPresenceTest")
    void initUsers(long count) {
        // Arrange
        when(this.jbstProperties.getSecurity()).thenReturn(JbstPropertySecurity.hardcoded());
        when(this.usersRepository.count()).thenReturn(count);

        // Act
        this.componentUnderTest.initUsers();

        // Assert
        verify(this.jbstProperties).getSecurity();
        verify(this.usersRepository).count();
        if (count == 0) {
            verify(this.abstractMockService).executeInheritedMethod();
        }
    }

    @ParameterizedTest
    @MethodSource("usersPresenceTest")
    void initInvitations(long count) {
        // Arrange
        when(this.jbstProperties.getSecurity()).thenReturn(JbstPropertySecurity.hardcoded());
        var username = Username.of("admin12");
        when(this.invitationsRepository.countByOwner(username)).thenReturn(count);

        // Act
        this.componentUnderTest.initInvitations();

        // Assert
        verify(this.jbstProperties).getSecurity();
        verify(this.invitationsRepository).countByOwner(username);
        if (count == 0) {
            verify(this.abstractMockService).executeInheritedMethod();
        }
    }
}
