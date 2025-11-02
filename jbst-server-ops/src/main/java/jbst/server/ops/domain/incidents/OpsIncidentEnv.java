package jbst.server.ops.domain.incidents;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.domain.properties.base.JbstPropertyRemoteServer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import java.net.URL;

// Lombok
@AllArgsConstructor
@Data
public class OpsIncidentEnv {
    private final String remoteHost;

    public OpsIncidentEnv(HttpServletRequest request) {
        this(
                request.getRemoteAddr()
        );
    }

    @SneakyThrows
    public static OpsIncidentEnv of(JbstPropertyRemoteServer remoteServer) {
        return new OpsIncidentEnv(new URL(remoteServer.getBaseURL()).getHost());
    }
}
