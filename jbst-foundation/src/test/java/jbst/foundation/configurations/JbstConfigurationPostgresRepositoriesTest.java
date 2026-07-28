package jbst.foundation.configurations;

import jbst.foundation.repositories.postgres.*;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstConfigurationPostgresRepositoriesTest {

    @Configuration
    @Import({
            TestJbstConfigurationPropertiesFixed.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {

        @Bean
        JbstPostgresSettingsRepository settingsRepository() {
            return mock(JbstPostgresSettingsRepository.class);
        }

        @Bean
        JbstPostgresInvitationsRepository invitationsRepository() {
            return mock(JbstPostgresInvitationsRepository.class);
        }

        @Bean
        JbstPostgresUsersTokensRepository usersTokensRepository() {
            return mock(JbstPostgresUsersTokensRepository.class);
        }

        @Bean
        JbstPostgresUsersRepository usersRepository() {
            return mock(JbstPostgresUsersRepository.class);
        }

        @Bean
        JbstPostgresUsersSessionsRepository usersSessionsRepository() {
            return mock(JbstPostgresUsersSessionsRepository.class);
        }

        @Bean
        JbstConfigurationPostgresRepositories applicationPostgresRepositories() {
            return new JbstConfigurationPostgresRepositories(
                    this.settingsRepository(),
                    this.invitationsRepository(),
                    this.usersTokensRepository(),
                    this.usersRepository(),
                    this.usersSessionsRepository()
            );
        }
    }

    private final JbstConfigurationPostgresRepositories componentUnderTest;

    @Test
    void beansTests() {
        // Act
        var methods = Stream.of(this.componentUnderTest.getClass().getMethods())
                .map(Method::getName)
                .toList();

        // Assert
        assertThat(methods)
                .hasSize(10)
                .contains("jbstPostgresRepositories");
    }
}
