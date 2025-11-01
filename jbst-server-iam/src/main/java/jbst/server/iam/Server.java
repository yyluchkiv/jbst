package jbst.server.iam;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.settings.JbstSettingsService;
import jbst.foundation.startup.JbstStartupEventListener;
import jbst.server.iam.configurations.ConfigurationServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import static jbst.foundation.domain.enums.Status.COMPLETED;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Slf4j
@Import({
        ConfigurationServer.class
})
@EnableConfigurationProperties({
        JbstProperties.class
})
@SpringBootApplication(scanBasePackages = {
        "jbst.server.iam.configurations"
})
public class Server extends JbstStartupEventListener {

    // Publishers
    private final JbstIncidentsPublisher incidentsPublisher;

    @Autowired
    public Server(JbstSettingsService settingsService, JbstIncidentsPublisher incidentsPublisher, JbstProperties jbstProperties) {
        super(settingsService, jbstProperties);
        this.incidentsPublisher = incidentsPublisher;
    }

    public static void main(String[] args) {
        var springApplication = new SpringApplication(Server.class);
        var applicationContext = springApplication.run(args);
        var jbstProperties = applicationContext.getBean(JbstProperties.class);
        LOGGER.info(JbstConstants.Logs.getServerContainer(jbstProperties.getServerConfigs(), COMPLETED));
    }

    @Override
    public void onStartup() {
        try {
            super.onStartup();
            LOGGER.info(JbstConstants.Logs.getServerStartup(this.jbstProperties.getServerConfigs(), COMPLETED));
        } catch (RuntimeException ex) {
            this.incidentsPublisher.publishThrowable(ex);
        }
    }
}
