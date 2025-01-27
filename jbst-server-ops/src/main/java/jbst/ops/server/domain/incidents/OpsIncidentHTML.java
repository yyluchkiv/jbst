package jbst.ops.server.domain.incidents;

public record OpsIncidentHTML(
        String name
) {

    public static OpsIncidentHTML opsAnyIncident() {
        return new OpsIncidentHTML("ops-any-incident");
    }
}
