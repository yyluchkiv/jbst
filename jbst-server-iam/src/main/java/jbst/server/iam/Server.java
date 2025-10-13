package jbst.server.iam;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.JbstSettingsOnInit;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import jbst.iam.essence.AbstractEssenceConstructor;
import jbst.iam.settings.JbstSettingsService;
import jbst.iam.startup.BaseStartupEventListener;
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
public class Server extends BaseStartupEventListener {

    // Publishers
    private final IncidentPublisher incidentPublisher;

    @Autowired
    public Server(
            JbstSettingsService jbstSettingsService,
            AbstractEssenceConstructor essenceConstructor,
            JbstSettingsOnInit jbstSettingsOnInit,
            JbstProperties jbstProperties,
            IncidentPublisher incidentPublisher
    ) {
        super(jbstSettingsService, essenceConstructor, jbstSettingsOnInit, jbstProperties);
        this.incidentPublisher = incidentPublisher;
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
            this.incidentPublisher.publishThrowable(ex);
        }
    }
}
