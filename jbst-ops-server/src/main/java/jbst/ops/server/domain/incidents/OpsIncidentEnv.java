package jbst.ops.server.domain.incidents;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.domain.properties.base.RemoteServer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import java.net.MalformedURLException;
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
    public static OpsIncidentEnv of(RemoteServer remoteServer) {
        return new OpsIncidentEnv(new URL(remoteServer.getBaseURL()).getHost());
    }

    @Deprecated
    public static OpsIncidentEnv of(RemoteServer remoteServer, HttpServletRequest request) {
        try {
            return new OpsIncidentEnv(new URL(remoteServer.getBaseURL()).getHost());
        } catch (MalformedURLException ex) {
            return new OpsIncidentEnv(request.getRemoteAddr());
        }
    }
}
