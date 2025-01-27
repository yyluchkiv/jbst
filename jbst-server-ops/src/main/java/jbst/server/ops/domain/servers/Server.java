package jbst.server.ops.domain.servers;

import jbst.foundation.domain.base.ServerName;
import jbst.foundation.feigns.spring.SpringBootClient;
import org.apache.commons.collections4.queue.CircularFifoQueue;

public record Server(
        Team team,
        ServerType type,
        ServerName name,
        String ipAddress,
        boolean ok,
        String health,
        boolean anyChanges,
        CircularFifoQueue<Boolean> upHistory,
        String onlineLastUpdatedAt,
        SpringBootClient.SpringBootActuatorInfo springBootActuatorInfo,
        boolean sshRequired,
        boolean fileSystemMetadataThresholdReached,
        boolean fileSystemMetadataProblems,
        ServerFileSystemMetadata fileSystemMetadata,
        String sshLastUpdatedAt
) {

}

