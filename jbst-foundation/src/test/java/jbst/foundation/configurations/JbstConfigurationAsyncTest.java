package jbst.foundation.configurations;

import jbst.foundation.domain.properties.JbstProperties;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.lang.reflect.Method;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jbst.foundation.utilities.processors.ProcessorsUtility.getNumOfCores;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstConfigurationAsyncTest {

    @Configuration
    @Import({
            TestJbstConfigurationPropertiesHardcoded.class,
            JbstConfigurationAsync.class
    })
    static class ContextConfiguration {

    }

    private final JbstProperties jbstProperties;

    private final JbstConfigurationAsync componentUnderTest;

    @Test
    void beansTests() {
        // Act
        var methods = Stream.of(this.componentUnderTest.getClass().getMethods())
                .map(Method::getName)
                .collect(Collectors.toList());

        // Assert
        assertThat(methods)
                .hasSize(16)
                .contains("getAsyncExecutor")
                .contains("getAsyncUncaughtExceptionHandler");
    }

    @Test
    void getAsyncExecutorTest() {
        // Arrange
        var async = this.jbstProperties.getAsync();

        // Act
        var actual = this.componentUnderTest.getAsyncExecutor();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getClass()).isEqualTo(ThreadPoolTaskExecutor.class);
        var threadPoolTaskExecutor = (ThreadPoolTaskExecutor) actual;
        assertThat(threadPoolTaskExecutor.getThreadNamePrefix()).isEqualTo(async.getThreadNamePrefix());
        assertThat(threadPoolTaskExecutor.getCorePoolSize()).isEqualTo(getNumOfCores(async.asThreadsCorePoolTuplePercentage()));
        assertThat(threadPoolTaskExecutor.getMaxPoolSize()).isEqualTo(getNumOfCores(async.asThreadsMaxPoolTuplePercentage()));
    }

    @Test
    void getAsyncUncaughtExceptionHandlerTest() {
        // Act
        var actual = this.componentUnderTest.getAsyncUncaughtExceptionHandler();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getClass()).isEqualTo(SimpleAsyncUncaughtExceptionHandler.class);
    }
}
