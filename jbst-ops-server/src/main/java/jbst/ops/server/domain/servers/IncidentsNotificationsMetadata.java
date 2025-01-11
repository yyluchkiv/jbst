package jbst.ops.server.domain.servers;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static jbst.foundation.domain.asserts.Asserts.assertTrueOrThrow;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.invalidAttribute;

@Deprecated(forRemoval = true)
// Lombok
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
public class IncidentsNotificationsMetadata {
    private final boolean present;
    private final Set<Email> emails;
    private final Set<Username> usernames;
    private final Map<String, IncidentNotificationConfigs> typesConfigs;

    public static IncidentsNotificationsMetadata incidentNotificationsMetadata(
            @NotNull Map<SubcontractorId, Subcontractor> team,
            @NotNull IncidentsNotificationsConfigs configs
    ) {
        assertTrueOrThrow(configs.enabled(), invalidAttribute("IncidentNotificationsMetadata.configs.enabled"));
        return new IncidentsNotificationsMetadata(
                true,
                configs.memberIds().stream().map(memberId -> team.get(memberId).email()).collect(toSet()),
                configs.usernames(),
                configs.typesConfigs()
        );
    }

    public static IncidentsNotificationsMetadata incidentNotificationsNoMetadata() {
        return new IncidentsNotificationsMetadata(
                false,
                new HashSet<>(),
                new HashSet<>(),
                new HashMap<>()
        );
    }

    public final Set<String> getEmailsAsStrings() {
        return this.emails.stream().map(Email::value).collect(toSet());
    }
}
