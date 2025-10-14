package jbst.iam.assistants.userdetails;

import jbst.foundation.assistants.userdetails.AbstractJwtUserDetailsService;
import jbst.foundation.repositories.UsersRepository;
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
import jbst.foundation.domain.base.Username;

import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AbstractJwtUserDetailsServiceTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        UsersRepository usersRepository() {
            return mock(UsersRepository.class);
        }

        @Bean
        AbstractJwtUserDetailsService jwtUserDetailsService() {
            return new AbstractJwtUserDetailsService(
                    this.usersRepository()
            ) {};
        }
    }

    private final UsersRepository usersRepository;

    private final AbstractJwtUserDetailsService jwtUserDetailsService;

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
    void getCurrentUsernameTest() {
        // Arrange
        var username = Username.random();

        // Act
        this.jwtUserDetailsService.loadUserByUsername(username.value());

        // Assert
        verify(this.usersRepository).loadUserByUsername(username);
    }
}
