package jbst.server.iam.postgres.services.impl;

import jbst.foundation.domain.databases.postgres.entities.PostgresDbUser;
import jbst.foundation.repositories.postgres.PostgresJbstUsersRepository;
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

import static jbst.foundation.utilities.random.EntityUtility.list345;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class UsersServiceImplTest {

    @Configuration
    static class ContextConfiguration {

        @Bean
        PostgresJbstUsersRepository usersRepository() {
            return mock(PostgresJbstUsersRepository.class);
        }

        @Bean
        UsersService userService() {
            return new UsersServiceImpl(
                    this.usersRepository()
            );
        }
    }

    private final PostgresJbstUsersRepository postgresUsersRepository;

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
        var postgresDbUsers = list345(PostgresDbUser.class);
        var expected = postgresDbUsers.stream().map(PostgresDbUser::asJwtUser).toList();
        when(this.postgresUsersRepository.findAll()).thenReturn(postgresDbUsers);

        // Act
        var actual = this.componentUnderTest.findAll();

        // Assert
        verify(this.postgresUsersRepository).findAll();
        assertThat(actual).isEqualTo(expected);
    }
}
