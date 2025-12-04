package jbst.foundation.assistants.userdetails;

import jbst.foundation.domain.base.Username;
import jbst.foundation.repositories.JbstUsersRepository;
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
class JbstUserDetailsServiceTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        JbstUsersRepository usersRepository() {
            return mock(JbstUsersRepository.class);
        }

        @Bean
        JbstUserDetailsService jwtUserDetailsService() {
            return new JbstUserDetailsService(
                    this.usersRepository()
            ) {};
        }
    }

    private final JbstUsersRepository usersRepository;

    private final JbstUserDetailsService jwtUserDetailsService;

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
