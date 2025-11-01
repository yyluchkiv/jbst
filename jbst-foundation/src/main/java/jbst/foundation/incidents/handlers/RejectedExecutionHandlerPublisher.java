package jbst.foundation.incidents.handlers;

import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RejectedExecutionHandlerPublisher implements RejectedExecutionHandler {

    // Incidents
    private final JbstIncidentsPublisher incidentsPublisher;

    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        var message = "Task " + runnable.toString() + " rejected from " + executor.toString();
        var rejectedExecutionException = new RejectedExecutionException(message);
        this.incidentsPublisher.publishThrowable(rejectedExecutionException);
        throw rejectedExecutionException;
    }
}
