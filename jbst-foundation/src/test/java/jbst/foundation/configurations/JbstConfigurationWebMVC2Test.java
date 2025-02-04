package jbst.foundation.configurations;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.configs.MvcConfigs;
import jbst.foundation.domain.properties.configs.mvc.CorsConfigs;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.lang.reflect.Method;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jbst.foundation.utilities.random.RandomUtility.randomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstConfigurationWebMVC2Test {

    @Configuration
    static class ContextConfiguration {
        @Bean
        JbstProperties jbstProperties() {
            var jbstProperties = mock(JbstProperties.class);
            var mvcConfigs = new MvcConfigs(
                    true,
                    randomString(),
                    new CorsConfigs(
                            "/api/**",
                            null,
                            null,
                            null,
                            false,
                            null
                    )
            );
            when(jbstProperties.getMvcConfigs()).thenReturn(mvcConfigs);
            return jbstProperties;
        }

        @Bean
        JbstConfigurationWebMVC applicationMVC() {
            return new JbstConfigurationWebMVC(
                    this.jbstProperties()
            );
        }
    }

    private final JbstConfigurationWebMVC componentUnderTest;

    @Test
    void beansTests() {
        // Act
        var methods = Stream.of(this.componentUnderTest.getClass().getMethods())
                .map(Method::getName)
                .collect(Collectors.toList());

        // Assert
        assertThat(methods)
                .hasSize(29)
                .contains("addCorsMappings");
    }

    @Test
    void addCorsMappingsEnabledTest() {
        // Arrange
        var corsRegistry = mock(CorsRegistry.class);
        var corsRegistration = mock(CorsRegistration.class);
        when(corsRegistry.addMapping("/api/**")).thenReturn(corsRegistration);

        // Act
        this.componentUnderTest.addCorsMappings(corsRegistry);

        // Assert
        verify(corsRegistry).addMapping("/api/**");
        verify(corsRegistration).allowCredentials(false);
        verifyNoMoreInteractions(
                corsRegistry,
                corsRegistration
        );
    }
}
