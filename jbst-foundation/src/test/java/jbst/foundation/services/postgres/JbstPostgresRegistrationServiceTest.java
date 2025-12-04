package jbst.foundation.services.postgres;

import jbst.foundation.repositories.postgres.JbstPostgresInvitationsRepository;
import jbst.foundation.repositories.postgres.JbstPostgresUsersRepository;
import jbst.foundation.repositories.postgres.JbstPostgresUsersTokensRepository;
import jbst.foundation.services.base.JbstUsersEmailsService;
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
class JbstPostgresRegistrationServiceTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        JbstUsersEmailsService usersEmailsService() {
            return mock(JbstUsersEmailsService.class);
        }

        @Bean
        JbstPostgresInvitationsRepository invitationsRepository() {
            return mock(JbstPostgresInvitationsRepository.class);
        }

        @Bean
        JbstPostgresUsersRepository userRepository() {
            return mock(JbstPostgresUsersRepository.class);
        }

        @Bean
        JbstPostgresUsersTokensRepository usersTokensRepository() {
            return mock(JbstPostgresUsersTokensRepository.class);
        }

        @Bean
        BCryptPasswordEncoder bCryptPasswordEncoder() {
            return mock(BCryptPasswordEncoder.class);
        }

        @Bean
        JbstPostgresRegistrationService registrationService() {
            return new JbstPostgresRegistrationService(
                    this.usersEmailsService(),
                    this.invitationsRepository(),
                    this.userRepository(),
                    this.usersTokensRepository(),
                    this.bCryptPasswordEncoder()
            ) {};
        }
    }

    private final JbstPostgresInvitationsRepository invitationsRepository;
    private final JbstPostgresUsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final JbstPostgresRegistrationService componentUnderTest;

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
