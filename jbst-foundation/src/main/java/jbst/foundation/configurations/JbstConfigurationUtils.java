package jbst.foundation.configurations;

import jakarta.annotation.PostConstruct;
import jbst.foundation.domain.properties.JbstProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        JbstProperties.class
})
@ComponentScan({
        "jbst.foundation.utils"
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationUtils {

    // Properties
    private final JbstProperties jbstProperties;

    @PostConstruct
    public void init() {
        this.jbstProperties.getUtilsConfigs().assertPropertyTree();
    }
}
