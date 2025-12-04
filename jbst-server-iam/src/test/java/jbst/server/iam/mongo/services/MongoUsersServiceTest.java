package jbst.server.iam.mongo.services;

import jbst.foundation.domain.databases.mongo.JbstMongoUser;
import jbst.foundation.repositories.mongo.JbstMongoUsersRepository;
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
class MongoUsersServiceTest {

    @Configuration
    static class ContextConfiguration {

        @Bean
        JbstMongoUsersRepository usersRepository() {
            return mock(JbstMongoUsersRepository.class);
        }

        @Bean
        UsersService userService() {
            return new MongoUsersService(
                    this.usersRepository()
            );
        }
    }

    private final JbstMongoUsersRepository usersRepository;

    private final UsersService componentUnderTest;

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

    @Test
    void findAll() {
        // Act
        var mongoDbUsers = list345(JbstMongoUser.class);
        var expected = mongoDbUsers.stream().map(JbstMongoUser::asJwtUser).toList();
        when(this.usersRepository.findAll()).thenReturn(mongoDbUsers);

        // Act
        var actual = this.componentUnderTest.findAll();

        // Assert
        verify(this.usersRepository).findAll();
        assertThat(actual).isEqualTo(expected);
    }
}
