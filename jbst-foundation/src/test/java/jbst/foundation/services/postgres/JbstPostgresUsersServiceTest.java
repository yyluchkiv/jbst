package jbst.foundation.services.postgres;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.JbstPropertyUserOnInit;
import jbst.foundation.repositories.postgres.JbstPostgresUsersRepository;
import jbst.foundation.repositories.postgres.JbstPostgresUsersTokensRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
class JbstPostgresUsersServiceTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        JbstPostgresUsersTokensRepository usersTokensRepository() {
            return mock(JbstPostgresUsersTokensRepository.class);
        }

        @Bean
        JbstPostgresUsersRepository userRepository() {
            return mock(JbstPostgresUsersRepository.class);
        }

        @Bean
        BCryptPasswordEncoder bCryptPasswordEncoder() {
            return mock(BCryptPasswordEncoder.class);
        }

        @Bean
        JbstProperties jbstProperties() {
            return mock(JbstProperties.class);
        }

        @Bean
        JbstPostgresUsersService usersService() {
            return new JbstPostgresUsersService(
                    this.usersTokensRepository(),
                    this.userRepository(),
                    this.bCryptPasswordEncoder(),
                    this.jbstProperties()
            );
        }
    }

    private final JbstPostgresUsersRepository usersRepository;

    private final JbstPostgresUsersService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.usersRepository
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
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
}
