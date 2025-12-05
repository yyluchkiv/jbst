package jbst.foundation.incidents.handlers;

import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.incidents.domain.JbstIncident;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.Arrays;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstAsyncUncaughtExceptionHandlerPublisher implements AsyncUncaughtExceptionHandler {

    // Publisher
    private final JbstIncidentsPublisher incidentsPublisher;

    @Override
    public void handleUncaughtException(@NotNull Throwable throwable, @NotNull Method method, Object @NotNull ... params) {
        this.incidentsPublisher.publishIncident(
                new JbstIncident(
                        throwable,
                        method,
                        Arrays.asList(params)
                )
        );
    }
}
