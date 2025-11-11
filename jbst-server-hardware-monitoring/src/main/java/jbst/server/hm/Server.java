package jbst.server.hm;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import static jbst.foundation.domain.enums.Status.COMPLETED;

@Slf4j
@SpringBootApplication(exclude = {
        // mongo
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
        // postgres
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class Server {

    public static void main(String[] args) {
        var springApplication = new SpringApplication(Server.class);
        var applicationContext = springApplication.run(args);
        var jbstProperties = applicationContext.getBean(JbstProperties.class);
        LOGGER.info(JbstConstants.Logs.getServerContainer(jbstProperties.getApp(), COMPLETED));
    }
}
