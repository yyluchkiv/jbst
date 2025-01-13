package jbst.ops.server.domain.configs.ssh;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public record SshConfigsLogs(
        List<String> destinations,
        String archive
) {
    @JsonIgnore
    public String getJoinedDestinations() {
        return String.join(" ", this.destinations);
    }
}
