package jbst.server.iam.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

@ConfigurationProperties(
        prefix = "jbst-server-iam",
        ignoreUnknownFields = false
)
@Data
public class ServerProperties implements PriorityOrdered {
    private JbstPropertyExample example;

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
