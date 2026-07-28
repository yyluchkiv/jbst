package jbst.foundation.configurations;

import jakarta.annotation.PostConstruct;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.feigns.telegram.JbstTelegram;
import jbst.foundation.incidents.clients.JbstIncidentClient;
import jbst.foundation.incidents.clients.JbstIncidentClientTypeLogger;
import jbst.foundation.incidents.clients.JbstIncidentClientTypeTelegram;
import jbst.foundation.incidents.handlers.JbstAsyncUncaughtExceptionHandlerPublisher;
import jbst.foundation.incidents.handlers.JbstErrorHandlerPublisher;
import jbst.foundation.incidents.handlers.JbstRejectedExecutionHandlerPublisher;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.incidents.services.JbstIncidentsSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ErrorHandler;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;

import static jbst.foundation.domain.hardware.JbstCPU.getNumOfCores;

@Configuration
@EnableConfigurationProperties({
        JbstProperties.class
})
@EnableAsync
@EnableScheduling
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationIncidents implements AsyncConfigurer {

    // Spring Publisher
    private final ApplicationEventPublisher applicationEventPublisher;
    // Properties
    private final JbstProperties jbstProperties;

    @PostConstruct
    public void init() {
        this.jbstProperties.getAsync().assertProperties();
        this.jbstProperties.getEvents().assertProperties();
        this.jbstProperties.getIncidentsManager().assertProperties();
    }

    // ================================================================================================================
    // Incidents: [SERVER, TELEGRAM]
    // ================================================================================================================
    @Bean
    @ConditionalOnProperty(value = "jbst.incidents-manager.enabled", havingValue = "true")
    JbstIncidentClient incidentClient() {
        var incidentsManagerType = this.jbstProperties.getIncidentsManager().getType();
        if (incidentsManagerType.isTelegram()) {
            var telegram = new JbstTelegram();
            telegram.initPragmatic(
                    this.jbstProperties.getIncidentsManager().getTelegram().getToken(),
                    this.jbstProperties.getIncidentsManager().getTelegram().getChatId()
            );
            telegram.start();
            return new JbstIncidentClientTypeTelegram(telegram, this.jbstProperties);
        }
        throw new JbstExceptions.UnreachableCode();
    }

    @Bean
    @ConditionalOnProperty(value = "jbst.incidents-manager.enabled", havingValue = "false", matchIfMissing = true)
    JbstIncidentClient incidentClientDisabled() {
        return new JbstIncidentClientTypeLogger();
    }

    // ================================================================================================================
    // Incidents: Pub+Sub
    // ================================================================================================================
    @Bean
    JbstIncidentsPublisher incidentsPublisher() {
        return new JbstIncidentsPublisher(this.applicationEventPublisher, this.jbstProperties);
    }

    @Bean
    JbstIncidentsSubscriber incidentsSubscriber(JbstIncidentClient incidentClient) {
        return new JbstIncidentsSubscriber(incidentClient);
    }

    // ================================================================================================================
    // Async
    // ================================================================================================================
    @Bean
    RejectedExecutionHandler rejectedExecutionHandler() {
        return new JbstRejectedExecutionHandlerPublisher(this.incidentsPublisher());
    }

    @Override
    public Executor getAsyncExecutor() {
        var async = this.jbstProperties.getAsync();
        if (async.isVirtualThreadsEnabled()) {
            var taskExecutor = new SimpleAsyncTaskExecutor(async.getThreadNamePrefix());
            taskExecutor.setVirtualThreads(true);
            return taskExecutor;
        } else {
            var taskExecutor = new ThreadPoolTaskExecutor();
            taskExecutor.setThreadNamePrefix(async.getThreadNamePrefix());
            taskExecutor.setCorePoolSize(getNumOfCores(async.asThreadsCorePoolTuplePercentage()));
            taskExecutor.setMaxPoolSize(getNumOfCores(async.asThreadsMaxPoolTuplePercentage()));
            taskExecutor.setRejectedExecutionHandler(this.rejectedExecutionHandler());
            taskExecutor.initialize();
            return taskExecutor;
        }
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new JbstAsyncUncaughtExceptionHandlerPublisher(this.incidentsPublisher());
    }

    // ================================================================================================================
    // Events
    // ================================================================================================================
    @Bean
    ErrorHandler errorHandlerPublisher() {
        return new JbstErrorHandlerPublisher(this.incidentsPublisher());
    }

    @SuppressWarnings("DuplicatedCode")
    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        var events = this.jbstProperties.getEvents();
        var eventMulticaster = new SimpleApplicationEventMulticaster();
        if (events.isVirtualThreadsEnabled()) {
            var taskExecutor = new SimpleAsyncTaskExecutor(events.getThreadNamePrefix());
            taskExecutor.setVirtualThreads(true);
            eventMulticaster.setTaskExecutor(taskExecutor);
        } else {
            var taskExecutor = new ThreadPoolTaskExecutor();
            taskExecutor.setThreadNamePrefix(events.getThreadNamePrefix());
            taskExecutor.setCorePoolSize(getNumOfCores(events.asThreadsCorePoolTuplePercentage()));
            taskExecutor.setMaxPoolSize(getNumOfCores(events.asThreadsMaxPoolTuplePercentage()));
            taskExecutor.initialize();
            eventMulticaster.setTaskExecutor(taskExecutor);
        }
        eventMulticaster.setErrorHandler(this.errorHandlerPublisher());
        return eventMulticaster;
    }
}
