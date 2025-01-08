package jbst.ops.server;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import jbst.ops.server.crons.CheckServersAnyChangesJob;
import jbst.ops.server.properties.OpsProperties;
import jbst.ops.server.services.IncidentsService;
import jbst.ops.server.services.MonitoringService;

import java.io.IOException;

import static jbst.foundation.domain.enums.Status.COMPLETED;
import static jbst.foundation.domain.enums.Status.STARTED;

@Slf4j
@EnableConfigurationProperties({
        JbstProperties.class,
        OpsProperties.class
})
@SpringBootApplication
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Server {

    // Jobs
    private final CheckServersAnyChangesJob checkServersAnyChangesJob;
    // Services
    private final IncidentsService incidentsService;
    private final MonitoringService monitoringService;
    // Properties
    private final JbstProperties jbstProperties;

    public static void main(String[] args) {
        var springApplication = new SpringApplication(Server.class);
        var applicationContext = springApplication.run(args);
        var jbstProperties = applicationContext.getBean(JbstProperties.class);
        LOGGER.info(JbstConstants.Logs.getServerContainer(jbstProperties.getServerConfigs(), COMPLETED));
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        try {
            LOGGER.info(JbstConstants.Logs.getServerStartup(this.jbstProperties.getServerConfigs(), STARTED));
            this.monitoringService.readServers();
//            this.checkServersAnyChangesJob.checkServersAnyChanges();
//            this.incidentsService.configureCleanCronJob();
            LOGGER.info(JbstConstants.Logs.getServerStartup(this.jbstProperties.getServerConfigs(), COMPLETED));
        } catch (IOException | RuntimeException ex) {
            LOGGER.error("Server startup failure", ex);
        }
    }
}
