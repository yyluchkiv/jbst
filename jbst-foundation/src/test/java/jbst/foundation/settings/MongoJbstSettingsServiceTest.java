package jbst.foundation.settings;

import jbst.foundation.configurations.TestJbstConfigurationPropertiesHardcoded;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.JbstPropertyUserOnInit;
import jbst.foundation.repositories.mongo.MongoJbstInvitationsRepository;
import jbst.foundation.repositories.mongo.MongoJbstSettingsRepository;
import jbst.foundation.repositories.mongo.MongoJbstUsersRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;

import static jbst.foundation.domain.random.JbstRandomEntities.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class MongoJbstSettingsServiceTest {

    @Configuration
    @Import({
            TestJbstConfigurationPropertiesHardcoded.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final JbstProperties jbstProperties;

        @Bean
        MongoJbstSettingsRepository settingsRepository() {
            return mock(MongoJbstSettingsRepository.class);
        }

        @Bean
        MongoJbstInvitationsRepository invitationsRepository() {
            return mock(MongoJbstInvitationsRepository.class);
        }

        @Bean
        MongoJbstUsersRepository userRepository() {
            return mock(MongoJbstUsersRepository.class);
        }

        @Bean
        MongoJbstSettingsService settingsService() {
            return new MongoJbstSettingsService(
                    this.settingsRepository(),
                    this.invitationsRepository(),
                    this.userRepository(),
                    this.jbstProperties
            );
        }
    }

    private final MongoJbstSettingsRepository settingsRepository;
    private final MongoJbstInvitationsRepository invitationsRepository;
    private final MongoJbstUsersRepository usersRepository;

    private final MongoJbstSettingsService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.settingsRepository,
                this.invitationsRepository,
                this.usersRepository
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.settingsRepository,
                this.invitationsRepository,
                this.usersRepository
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void initUsers() {
        // Arrange
        var users = list345(JbstPropertyUserOnInit.class);

        // Act
        var actual = this.componentUnderTest.initUsers(users);

        // Assert
        var userAC = ArgumentCaptor.forClass(List.class);
        verify(this.usersRepository).saveAll(userAC.capture());
        assertThat(actual)
                .isEqualTo(users.size())
                .isEqualTo(userAC.getValue().size());
    }

    @SuppressWarnings("unchecked")
    @Test
    void initInvitations() {
        // Arrange
        var user = entity(JbstPropertyUserOnInit.class);
        var authorities = set345(SimpleGrantedAuthority.class);

        // Act
        this.componentUnderTest.initInvitations(user, authorities);

        // Assert
        var userAC = ArgumentCaptor.forClass(List.class);
        verify(this.invitationsRepository).saveAll(userAC.capture());
        assertThat(userAC.getValue()).hasSize(10);
    }
}
