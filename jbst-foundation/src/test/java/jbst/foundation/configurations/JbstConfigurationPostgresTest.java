package jbst.foundation.configurations;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.repositories.postgres.JbstPostgresInvitationsRepository;
import jbst.foundation.repositories.postgres.JbstPostgresSettingsRepository;
import jbst.foundation.repositories.postgres.JbstPostgresUsersRepository;
import jbst.foundation.repositories.postgres.JbstPostgresUsersSessionsRepository;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstConfigurationPostgresTest {

    @Configuration
    @Import({
            TestJbstConfigurationPropertiesHardcoded.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final JbstProperties jbstProperties;

        @Bean
        JbstConfigurationPostgres jbstConfigurationPostgres() {
            return new JbstConfigurationPostgres(
                    mock(JbstPostgresSettingsRepository.class),
                    mock(JbstPostgresInvitationsRepository.class),
                    mock(JbstPostgresUsersRepository.class),
                    mock(JbstPostgresUsersSessionsRepository.class),
                    this.jbstProperties
            );
        }
    }

    private final JbstConfigurationPostgres componentUnderTest;

    @Test
    void beansTests() {
        // Act
        var methods = Stream.of(this.componentUnderTest.getClass().getMethods())
                .map(Method::getName)
                .collect(Collectors.toSet());

        // Assert
        assertThat(methods).hasSize(7);
    }
}
