package jbst.foundation.configurations;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.configs.JbstPropertyEvents;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jbst.foundation.domain.constants.JbstConstants.Numbers.BigDecimals.HUNDRED;
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
        var taskExecutor = ReflectionTestUtils.getField(actual, "taskExecutor");
        assertThat(taskExecutor).isInstanceOf(SimpleAsyncTaskExecutor.class);
        var simpleAsyncTaskExecutor = (SimpleAsyncTaskExecutor) taskExecutor;
        assertThat(simpleAsyncTaskExecutor.getThreadNamePrefix()).isEqualTo("jbst-events");
        var virtual = CompletableFuture.supplyAsync(() -> Thread.currentThread().isVirtual(), simpleAsyncTaskExecutor).join();
        assertThat(virtual).isTrue();
    }

    @Test
    void simpleApplicationEventMulticasterPlatformThreadsTest() {
        // Arrange
        var properties = new JbstProperties();
        properties.setEvents(new JbstPropertyEvents(false, "jbst-events", new BigDecimal("75"), HUNDRED));
        var configuration = new JbstConfigurationEvents(properties);

        // Act
        var actual = configuration.simpleApplicationEventMulticaster();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getClass()).isEqualTo(SimpleApplicationEventMulticaster.class);
        var taskExecutor = ReflectionTestUtils.getField(actual, "taskExecutor");
        assertThat(taskExecutor).isInstanceOf(ThreadPoolTaskExecutor.class);
        var threadPoolTaskExecutor = (ThreadPoolTaskExecutor) taskExecutor;
        assertThat(threadPoolTaskExecutor.getThreadNamePrefix()).isEqualTo("jbst-events");
    }
}
