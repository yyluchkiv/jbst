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
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.lang.reflect.Method;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstConfigurationWebMVC1Test {

    @Configuration
    static class ContextConfiguration {
        @Bean
        JbstProperties jbstProperties() {
            var jbstProperties = mock(JbstProperties.class);
            var mvcConfigs = new MvcConfigs(
                    false,
                    "/jbst/security",
                    new CorsConfigs(
                            "/api/**",
                            new String[] { "http://localhost:8080", "http://localhost:8081" },
                            new String[] { "GET", "POST" },
                            new String[] { "Access-Control-Allow-Origin" },
                            true,
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
                .hasSize(28)
                .contains("addCorsMappings");
    }

    @Test
    void addCorsMappingsDisabledTest() {
        // Arrange
        var corsRegistry = mock(CorsRegistry.class);

        // Act
        this.componentUnderTest.addCorsMappings(corsRegistry);

        // Assert
        verifyNoMoreInteractions(
                corsRegistry
        );
    }
}
