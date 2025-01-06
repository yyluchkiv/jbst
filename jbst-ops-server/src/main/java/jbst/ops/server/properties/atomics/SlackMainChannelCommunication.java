package jbst.ops.server.properties.atomics;

import lombok.*;

// Lombok
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class SlackMainChannelCommunication {
    private boolean enabled;
    private String channel;
}
