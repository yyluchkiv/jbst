package jbst.foundation.configurations;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
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
class JbstConfigurationEventsTest {

    @Configuration
    @Import({
            TestJbstConfigurationPropertiesFixed.class,
            JbstConfigurationEvents.class
    })
    static class ContextConfiguration {

    }

    private final JbstConfigurationEvents componentUnderTest;

    @Test
    void beansTests() {
        // Act
        var methods = Stream.of(this.componentUnderTest.getClass().getMethods())
                .map(Method::getName)
                .collect(Collectors.toList());

        // Assert
        assertThat(methods)
                .hasSize(15)
                .contains("simpleApplicationEventMulticaster");
    }

    @Test
    void simpleApplicationEventMulticasterTest() {
        // Act
        var actual = this.componentUnderTest.simpleApplicationEventMulticaster();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getClass()).isEqualTo(SimpleApplicationEventMulticaster.class);
    }
}
