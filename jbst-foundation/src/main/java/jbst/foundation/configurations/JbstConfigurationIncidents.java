package jbst.foundation.configurations;

import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import jakarta.annotation.PostConstruct;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.incidents.feigns.clients.JbstIncidentClient;
import jbst.foundation.incidents.feigns.definitions.JbstIncidentClientDefinition;
import jbst.foundation.incidents.feigns.definitions.JbstIncidentClientDefinitionSlf4J;
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
    // Incidents: HTTP
    // ================================================================================================================
    @Bean
    @ConditionalOnProperty(value = "jbst.incidents-manager.enabled", havingValue = "true")
    JbstIncidentClientDefinition incidentClientDefinition() {
        var incidentServer = this.jbstProperties.getIncidentsManager().getRemoteServer();
        return Feign.builder()
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .requestInterceptor(
                        new BasicAuthRequestInterceptor(
                                incidentServer.getCredentials().username().value(),
                                incidentServer.getCredentials().password().value()
                        )
                )
                .target(JbstIncidentClientDefinition.class, incidentServer.getBaseURL());
    }

    @Bean
    @ConditionalOnProperty(value = "jbst.incidents-manager.enabled", havingValue = "false", matchIfMissing = true)
    JbstIncidentClientDefinition incidentClientDefinitionSlf4j() {
        return new JbstIncidentClientDefinitionSlf4J();
    }

    @Bean
    JbstIncidentClient incidentClient(JbstIncidentClientDefinition incidentClientDefinition) {
        return new JbstIncidentClient(incidentClientDefinition);
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
        var taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setThreadNamePrefix(async.getThreadNamePrefix());
        taskExecutor.setCorePoolSize(getNumOfCores(async.asThreadsCorePoolTuplePercentage()));
        taskExecutor.setMaxPoolSize(getNumOfCores(async.asThreadsMaxPoolTuplePercentage()));
        taskExecutor.setRejectedExecutionHandler(this.rejectedExecutionHandler());
        taskExecutor.initialize();
        return taskExecutor;
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
        var taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setThreadNamePrefix(events.getThreadNamePrefix());
        taskExecutor.setCorePoolSize(getNumOfCores(events.asThreadsCorePoolTuplePercentage()));
        taskExecutor.setMaxPoolSize(getNumOfCores(events.asThreadsMaxPoolTuplePercentage()));
        taskExecutor.initialize();
        var eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(taskExecutor);
        eventMulticaster.setErrorHandler(this.errorHandlerPublisher());
        return eventMulticaster;
    }
}
