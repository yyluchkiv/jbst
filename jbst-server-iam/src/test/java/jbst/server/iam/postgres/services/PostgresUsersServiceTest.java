package jbst.server.iam.postgres.services;

import jbst.foundation.domain.databases.postgres.entities.JbstPostgresUser;
import jbst.foundation.repositories.postgres.JbstPostgresUsersRepository;
import jbst.server.iam.base.services.UsersService;
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

import static jbst.foundation.domain.random.JbstRandomEntities.list345;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class PostgresUsersServiceTest {

    @Configuration
    static class ContextConfiguration {

        @Bean
        JbstPostgresUsersRepository usersRepository() {
            return mock(JbstPostgresUsersRepository.class);
        }

        @Bean
        UsersService userService() {
            return new PostgresUsersService(
                    this.usersRepository()
            );
        }
    }

    private final JbstPostgresUsersRepository postgresUsersRepository;

    private final UsersService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.postgresUsersRepository
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.postgresUsersRepository
        );
    }

    @Test
    void findAll() {
        // Act
        var postgresDbUsers = list345(JbstPostgresUser.class);
        var expected = postgresDbUsers.stream().map(JbstPostgresUser::asJwtUser).toList();
        when(this.postgresUsersRepository.findAll()).thenReturn(postgresDbUsers);

        // Act
        var actual = this.componentUnderTest.findAll();

        // Assert
        verify(this.postgresUsersRepository).findAll();
        assertThat(actual).isEqualTo(expected);
    }
}
