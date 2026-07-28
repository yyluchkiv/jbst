package jbst.foundation.configurations;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.tests.classes.ClassAnnotatedAbstractFrameworkBaseSecurityResource;
import jbst.foundation.tests.classes.ClassNotAnnotatedAbstractFrameworkBaseSecurityResource;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;

import java.lang.reflect.Method;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstConfigurationSecurityJwtWebMVCTest {

    @Configuration
    @Import(
            TestJbstConfigurationPropertiesFixed.class
    )
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final JbstProperties jbstProperties;

        @Bean
        JbstConfigurationSecurityJwtWebMVC applicationMVC() {
            return new JbstConfigurationSecurityJwtWebMVC(
                    this.jbstProperties
            );
        }
    }

    private final JbstConfigurationSecurityJwtWebMVC componentUnderTest;

    @Test
    void beansTests() {
        // Act
        var methods = Stream.of(this.componentUnderTest.getClass().getMethods())
                .map(Method::getName)
                .toList();

        // Assert
        assertThat(methods)
                .hasSize(29)
                .contains("addCorsMappings")
                .contains("configurePathMatch");
    }

    @SuppressWarnings("unchecked")
    @Test
    void configurePathMatchTest() {
        // Arrange
        var configurer = mock(PathMatchConfigurer.class);

        // Act
        this.componentUnderTest.configurePathMatch(configurer);

        // Assert
        var prefixAC = ArgumentCaptor.forClass(String.class);
        var predicateAC = ArgumentCaptor.forClass(Predicate.class);
        verify(configurer).addPathPrefix(prefixAC.capture(), predicateAC.capture());
        assertThat(prefixAC.getValue()).isEqualTo("/jbst/security");
        Predicate<Class<?>> predicate = predicateAC.getValue();
        assertThat(predicate.test(ClassAnnotatedAbstractFrameworkBaseSecurityResource.class)).isTrue();
        assertThat(predicate.test(ClassNotAnnotatedAbstractFrameworkBaseSecurityResource.class)).isFalse();
    }
}
