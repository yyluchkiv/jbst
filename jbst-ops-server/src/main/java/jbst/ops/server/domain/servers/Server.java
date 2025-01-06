package jbst.ops.server.domain.servers;

import jbst.foundation.domain.base.ObjectId;
import jbst.foundation.domain.base.ServerName;
import jbst.foundation.feigns.spring.domain.SpringBootActuatorInfo;
import org.apache.commons.collections4.queue.CircularFifoQueue;

public record Server(
        ObjectId id,
        Team team,
        ServerType type,
        ServerName name,
        String ipAddress,
        boolean ok,
        String health,
        boolean anyChanges,
        CircularFifoQueue<Boolean> upHistory,
        String onlineLastUpdatedAt,
        SpringBootActuatorInfo springBootActuatorInfo,
        boolean sshRequired,
        boolean fileSystemMetadataThresholdReached,
        boolean fileSystemMetadataProblems,
        FileSystemMetadata fileSystemMetadata,
        String sshLastUpdatedAt
) {
}

