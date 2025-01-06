package jbst.ops.server.properties.atomics;

import lombok.*;
import jbst.ops.server.domain.authorities.Permission;

import java.util.List;

// Lombok
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Command {
    private String key;
    private String description;
    private List<Permission> permissions;
}
