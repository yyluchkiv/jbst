package jbst.server.hm.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

@ConfigurationProperties(
        prefix = "jbst-server-hardware-monitoring",
        ignoreUnknownFields = false
)
@Data
public class ServerProperties implements PriorityOrdered {
    private JbstPropertyServer server;

    @SuppressWarnings("unused")
    public static ServerProperties fixed() {
        var properties = new ServerProperties();
        properties.setServer(JbstPropertyServer.fixed());
        return properties;
    }

    @SuppressWarnings("unused")
    public static ServerProperties random() {
        var properties = new ServerProperties();
        properties.setServer(JbstPropertyServer.random());
        return properties;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
