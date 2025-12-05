package jbst.foundation.incidents.handlers;

import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ErrorHandler;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstErrorHandlerPublisher implements ErrorHandler {

    // Publisher
    private final JbstIncidentsPublisher incidentsPublisher;

    @Override
    public void handleError(@NotNull Throwable throwable) {
        this.incidentsPublisher.publishThrowable(throwable);
    }
}
