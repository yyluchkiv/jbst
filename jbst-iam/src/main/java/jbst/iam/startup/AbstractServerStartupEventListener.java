package jbst.iam.startup;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

public interface AbstractServerStartupEventListener {
    @EventListener(ApplicationStartedEvent.class)
    void onStartup();
}
