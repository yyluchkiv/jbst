package jbst.ops.server.domain.servers;

import jbst.foundation.domain.base.Username;

import java.util.Map;
import java.util.Set;

public record IncidentsNotificationsConfigs(
        boolean enabled,
        Set<SubcontractorId> memberIds,
        Set<Username> usernames,
        Map<String, IncidentNotificationConfigs> typesConfigs
) {
}
