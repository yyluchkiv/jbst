package jbst.iam.server.mongodb.services.impl;

import jbst.iam.domain.mongodb.MongoDbUser;
import jbst.iam.repositories.mongodb.MongoUsersRepository;
import jbst.iam.server.base.services.UsersService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static jbst.foundation.utilities.random.EntityUtility.list345;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class UsersServiceImplTest {

    @Configuration
    static class ContextConfiguration {

        @Bean
        MongoUsersRepository usersRepository() {
            return mock(MongoUsersRepository.class);
        }

        @Bean
        UsersService userService() {
            return new UsersServiceImpl(
                    this.usersRepository()
            );
        }
    }

    private final MongoUsersRepository usersRepository;

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
        var mongoDbUsers = list345(MongoDbUser.class);
        var expected = mongoDbUsers.stream().map(MongoDbUser::asJwtUser).toList();
        when(this.usersRepository.findAll()).thenReturn(mongoDbUsers);

        // Act
        var actual = this.componentUnderTest.findAll();

        // Assert
        verify(this.usersRepository).findAll();
        assertThat(actual).isEqualTo(expected);
    }
}
