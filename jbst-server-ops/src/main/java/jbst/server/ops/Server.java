package jbst.server.ops;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.server.ops.jobs.ServersJob;
import jbst.server.ops.properties.OpsProperties;
import jbst.server.ops.services.IncidentsService;
import jbst.server.ops.services.MonitoringService;
import jbst.server.ops.slack.SlackBotsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;

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

    // Services
    private final SlackBotsService slackBotsService;
    private final MonitoringService monitoringService;
    private final IncidentsService incidentsService;
    // Jobs
    private final ServersJob serversJob;
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
            this.slackBotsService.initialize();
            this.monitoringService.initialize();
            this.serversJob.scheduleAnyChangesNotification();
            this.incidentsService.configureCleanCronJob();
            LOGGER.info(JbstConstants.Logs.getServerStartup(this.jbstProperties.getServerConfigs(), COMPLETED));
        } catch (RuntimeException ex) {
            LOGGER.error("Server startup failure", ex);
        }
    }
}
