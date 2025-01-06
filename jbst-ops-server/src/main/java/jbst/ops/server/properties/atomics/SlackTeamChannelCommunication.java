package jbst.ops.server.properties.atomics;

import jbst.ops.server.domain.servers.Team;
import lombok.*;

// Lombok
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class SlackTeamChannelCommunication {
    private Team team;
    private SlackMainChannelCommunication communication;
}
