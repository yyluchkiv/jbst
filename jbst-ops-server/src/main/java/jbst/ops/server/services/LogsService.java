package jbst.ops.server.services;

import feign.Response;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jbst.ops.server.domain.computed.ComputedServer;
import jbst.ops.server.domain.servers.Team;
import jbst.ops.server.exceptions.SshSessionException;

import java.io.IOException;
import java.io.OutputStream;

@Deprecated
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LogsService {

    // Services
    private final IncidentsProcessor incidentsProcessor;
    private final MonitoringService monitoringService;
    private final SshService sshService;

    public final Response attachArchivedLogs(Integer serverId, HttpServletResponse response) {
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=logs.zip");
            response.setStatus(HttpServletResponse.SC_OK);
            var server = this.monitoringService.getComputedServer(serverId);
            attachArchivedLogsByServer(server, response.getOutputStream());
            response.flushBuffer();
        } catch (SshSessionException | IOException ex) {
            this.incidentsProcessor.processIncident(ex);
        }
        return null;
    }

    public final Response attachArchivedLogs(Integer serverId, Team team, HttpServletResponse response) {
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=logs.zip");
            response.setStatus(HttpServletResponse.SC_OK);
            var server = this.monitoringService.getComputedServer(serverId, team);
            attachArchivedLogsByServer(server, response.getOutputStream());
            response.flushBuffer();
        } catch (SshSessionException | IOException ex) {
            this.incidentsProcessor.processIncident(ex);
        }
        return null;
    }

    // ------------------------------------------------------------------------------------------------------
    // Private Methods
    // ------------------------------------------------------------------------------------------------------
    private void attachArchivedLogsByServer(ComputedServer server, OutputStream os) throws SshSessionException {
        LOGGER.info("Attach Logs. ServerId `{}`, Server `{}` connecting", server.getId(), server.getName());
        var sshSession = this.sshService.getSession(server.getServerSshConfigs());
        if (sshSession.getSession().present()) {
            var session = sshSession.getSession().value();
            this.sshService.executeCmd(session, "zip -r logs.zip " + server.getServerSshConfigs().getLogs().getJoinedDestinations());
            this.sshService.attachLogs(session, os, server.getServerSshConfigs().getLogs().archive());
            this.sshService.executeCmd(session, "rm -rf logs.zip");
            session.disconnect();
            LOGGER.info("Attach Logs. ServerId `{}`, Server `{}` disconnected", server.getId(), server.getName());
        } else {
            LOGGER.error("Attach Logs. ServerId `{}`, Server `{}` connection failure", server.getId(), server.getName(), sshSession.getThrowable().value());
        }
    }
}
