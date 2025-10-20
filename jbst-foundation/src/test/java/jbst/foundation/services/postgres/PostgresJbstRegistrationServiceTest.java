package jbst.foundation.services.postgres;

import jbst.foundation.repositories.postgres.PostgresJbstInvitationsRepository;
import jbst.foundation.repositories.postgres.PostgresJbstUsersRepository;
import jbst.foundation.repositories.postgres.PostgresJbstUsersTokensRepository;
import jbst.foundation.services.base.UsersEmailsService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class PostgresJbstRegistrationServiceTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        UsersEmailsService usersEmailsService() {
            return mock(UsersEmailsService.class);
        }

        @Bean
        PostgresJbstInvitationsRepository invitationsRepository() {
            return mock(PostgresJbstInvitationsRepository.class);
        }

        @Bean
        PostgresJbstUsersRepository userRepository() {
            return mock(PostgresJbstUsersRepository.class);
        }

        @Bean
        PostgresJbstUsersTokensRepository usersTokensRepository() {
            return mock(PostgresJbstUsersTokensRepository.class);
        }

        @Bean
        BCryptPasswordEncoder bCryptPasswordEncoder() {
            return mock(BCryptPasswordEncoder.class);
        }

        @Bean
        PostgresJbstRegistrationService registrationService() {
            return new PostgresJbstRegistrationService(
                    this.usersEmailsService(),
                    this.invitationsRepository(),
                    this.userRepository(),
                    this.usersTokensRepository(),
                    this.bCryptPasswordEncoder()
            ) {};
        }
    }

    private final PostgresJbstInvitationsRepository invitationsRepository;
    private final PostgresJbstUsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final PostgresJbstRegistrationService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.invitationsRepository,
                this.usersRepository,
                this.bCryptPasswordEncoder
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.invitationsRepository,
                this.usersRepository,
                this.bCryptPasswordEncoder
        );
    }

    @Test
    void verifyTransactionalAnnotationTest() {
        // Assert
        Arrays.stream(this.componentUnderTest.getClass().getMethods())
                .filter(method -> method.getName().contains("register1"))
                .forEach(method -> assertThat(method.isAnnotationPresent(Transactional.class)).isTrue());
    }
}
