package jbst.server.iam.configurations;

import jbst.iam.configurations.JbstConfigurationPostgres;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Profile("postgres")
@Configuration
@Import({
        JbstConfigurationPostgres.class
})
@ComponentScan({
        "jbst.server.iam.postgres"
})
public class ConfigurationServerPostgres {
}
