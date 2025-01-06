package jbst.ops.server.domain.incidents;

public record IncidentTemplate(
        String name
) {

    public static IncidentTemplate opsAnyIncident() {
        return new IncidentTemplate("ops-any-incident");
    }
}
