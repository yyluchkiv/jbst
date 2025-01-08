package jbst.ops.server.domain.computed;

import java.util.List;

public record ComputedServers(
        List<ServerInfinityTimerTask> values
) {
}
