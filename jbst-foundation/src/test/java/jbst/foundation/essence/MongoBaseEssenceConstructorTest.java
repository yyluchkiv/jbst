package jbst.foundation.essence;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.configurations.TestJbstConfigurationPropertiesHardcoded;
import jbst.foundation.domain.properties.base.DefaultUser;
import jbst.foundation.essense.EssenceConstructor;
import jbst.foundation.essense.MongoBaseEssenceConstructor;
import jbst.foundation.repositories.mongo.MongoInvitationsRepository;
import jbst.foundation.repositories.mongo.MongoUsersRepository;
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

import static jbst.foundation.utilities.random.EntityUtility.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class MongoBaseEssenceConstructorTest {

    @Configuration
    @Import({
            TestJbstConfigurationPropertiesHardcoded.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final JbstProperties jbstProperties;

        @Bean
        MongoInvitationsRepository invitationsRepository() {
            return mock(MongoInvitationsRepository.class);
        }

        @Bean
        MongoUsersRepository userRepository() {
            return mock(MongoUsersRepository.class);
        }

        @Bean
        EssenceConstructor essenceConstructor() {
            return new MongoBaseEssenceConstructor(
                    this.invitationsRepository(),
                    this.userRepository(),
                    this.jbstProperties
            );
        }
    }

    private final MongoInvitationsRepository invitationsRepository;
    private final MongoUsersRepository usersRepository;

    private final EssenceConstructor componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.invitationsRepository,
                this.usersRepository
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.invitationsRepository,
                this.usersRepository
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void saveDefaultUsersTest() {
        // Arrange
        var defaultUsers = list345(DefaultUser.class);

        // Act
        var actual = this.componentUnderTest.saveDefaultUsers(defaultUsers);

        // Assert
        var userAC = ArgumentCaptor.forClass(List.class);
        verify(this.usersRepository).saveAll(userAC.capture());
        assertThat(actual)
                .isEqualTo(defaultUsers.size())
                .isEqualTo(userAC.getValue().size());
    }

    @SuppressWarnings("unchecked")
    @Test
    void saveInvitationsTest() {
        // Arrange
        var defaultUser = entity(DefaultUser.class);
        var authorities = set345(SimpleGrantedAuthority.class);

        // Act
        this.componentUnderTest.saveInvitations(defaultUser, authorities);

        // Assert
        var userAC = ArgumentCaptor.forClass(List.class);
        verify(this.invitationsRepository).saveAll(userAC.capture());
        assertThat(userAC.getValue()).hasSize(10);
    }
}
