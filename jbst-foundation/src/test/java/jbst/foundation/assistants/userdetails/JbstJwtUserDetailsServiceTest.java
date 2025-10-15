package jbst.foundation.assistants.userdetails;

import jbst.foundation.domain.base.Username;
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

import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstJwtUserDetailsServiceTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        UsersRepository usersRepository() {
            return mock(UsersRepository.class);
        }

        @Bean
        JbstJwtUserDetailsService jwtUserDetailsService() {
            return new JbstJwtUserDetailsService(
                    this.usersRepository()
            ) {};
        }
    }

    private final UsersRepository usersRepository;

    private final JbstJwtUserDetailsService jwtUserDetailsService;

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
