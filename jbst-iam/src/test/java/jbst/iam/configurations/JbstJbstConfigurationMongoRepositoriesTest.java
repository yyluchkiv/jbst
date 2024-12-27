package jbst.iam.configurations;

import jbst.foundation.configurations.TestJbstConfigurationPropertiesHardcoded;
import jbst.foundation.domain.properties.JbstProperties;
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

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstJbstConfigurationMongoRepositoriesTest {

    @Configuration
    @Import({
            TestJbstConfigurationPropertiesHardcoded.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {

        private final JbstProperties jbstProperties;

        @Bean
        JbstConfigurationMongoRepositories applicationMongoRepositories() {
            return new JbstConfigurationMongoRepositories(
                    this.jbstProperties
            );
        }
    }

    private final JbstConfigurationMongoRepositories componentUnderTest;

    @Test
    void beansTests() {
        // Act
        var methods = Stream.of(this.componentUnderTest.getClass().getMethods())
                .map(Method::getName)
                .collect(Collectors.toList());

        // Assert
        assertThat(methods)
                .hasSize(13)
                .contains("jbstMongoRepositories")
                .contains("jbstMongoClient")
                .contains("jbstMongoDatabaseFactory")
                .contains("jbstMongoTemplate");
        assertThat(this.componentUnderTest.jbstMongoClient()).isNotNull();
        assertThat(this.componentUnderTest.jbstMongoDatabaseFactory()).isNotNull();
        assertThat(this.componentUnderTest.jbstMongoTemplate()).isNotNull();
    }
}
