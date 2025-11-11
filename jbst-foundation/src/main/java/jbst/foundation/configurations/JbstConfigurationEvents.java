package jbst.foundation.configurations;

import jakarta.annotation.PostConstruct;
import jbst.foundation.domain.properties.JbstProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.support.TaskUtils;

import static jbst.foundation.utilities.processors.ProcessorsUtility.getNumOfCores;

@Configuration
@EnableConfigurationProperties({
        JbstProperties.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationEvents {

    // Properties
    private final JbstProperties jbstProperties;

    @PostConstruct
    public void init() {
        this.jbstProperties.getEvents().assertProperties();
    }

    @SuppressWarnings("DuplicatedCode")
    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        var events = this.jbstProperties.getEvents();
        var taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setThreadNamePrefix(events.getThreadNamePrefix());
        taskExecutor.setCorePoolSize(getNumOfCores(events.asThreadsCorePoolTuplePercentage()));
        taskExecutor.setMaxPoolSize(getNumOfCores(events.asThreadsMaxPoolTuplePercentage()));
        taskExecutor.initialize();
        var eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(taskExecutor);
        eventMulticaster.setErrorHandler(TaskUtils.LOG_AND_SUPPRESS_ERROR_HANDLER);
        return eventMulticaster;
    }
}
