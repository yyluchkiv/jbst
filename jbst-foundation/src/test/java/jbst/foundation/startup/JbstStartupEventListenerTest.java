package jbst.foundation.startup;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.JbstPropertyAuthority;
import jbst.foundation.domain.properties.base.JbstPropertyInvitationsOnInit;
import jbst.foundation.domain.properties.base.JbstPropertyUsersOnInit;
import jbst.foundation.domain.properties.configs.JbstPropertySecurity;
import jbst.foundation.domain.properties.configs.JbstPropertyApp;
import jbst.foundation.domain.properties.configs.security.JbstPropertySecurityAuthorities;
import jbst.foundation.domain.properties.configs.security.JbstPropertySecurityEssence;
import jbst.foundation.settings.JbstSettingsService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstStartupEventListenerTest {

    private static Stream<Arguments> onStartupTest() {
        return Stream.of(
                Arguments.of(true, true),
                Arguments.of(true, false),
                Arguments.of(false, true),
                Arguments.of(false, false)
        );
    }

    @Configuration
    static class ContextConfiguration {
        @Bean
        JbstSettingsService jbstSettingsService() {
            return mock(JbstSettingsService.class);
        }

        @Bean
        JbstProperties jbstProperties() {
            return mock(JbstProperties.class);
        }

        @Bean
        JbstStartupEventListener baseStartupEventListener() {
            return new JbstStartupEventListener(
                    this.jbstSettingsService(),
                    this.jbstProperties()
            );
        }
    }

    private final JbstSettingsService settingsService;
    private final JbstProperties jbstProperties;

    private final JbstStartupEventListener componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.settingsService,
                this.jbstProperties
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.settingsService,
                this.jbstProperties
        );
    }

    @ParameterizedTest
    @MethodSource("onStartupTest")
    void onStartupTest(boolean isUsersEnabled, boolean isInvitationsEnabled) {
        // Arrange
        var securityJwtConfigs = new JbstPropertySecurity(
                new JbstPropertySecurityAuthorities(
                        "jbst",
                        Set.of(
                                new JbstPropertyAuthority("admin"),
                                new JbstPropertyAuthority("user")
                        )
                ),
                null,
                new JbstPropertySecurityEssence(
                        new JbstPropertyUsersOnInit(
                                isUsersEnabled,
                                new ArrayList<>()
                        ),
                        new JbstPropertyInvitationsOnInit(
                                isInvitationsEnabled
                        )
                ),
                null,
                null,
                null,
                null,
                null,
                null
        );
        when(this.jbstProperties.getApp()).thenReturn(JbstPropertyApp.hardcoded());
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);

        // Act
        this.componentUnderTest.onStartup();

        // Assert
        verify(this.jbstProperties, times(2)).getSecurityJwtConfigs();
        if (isUsersEnabled) {
            verify(this.settingsService).initUsers();
        }
        if (isInvitationsEnabled) {
            verify(this.settingsService).initInvitations();
        }
        reset(
                this.settingsService,
                this.jbstProperties
        );
    }
}
