package jbst.ops.server.properties.atomics;

import lombok.*;
import jbst.ops.server.domain.keywords.KeywordCommand;

import java.util.Map;

// Lombok
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ServiceConfig {
    private String rootCmd;
    private Map<KeywordCommand, Command> commands;
}
