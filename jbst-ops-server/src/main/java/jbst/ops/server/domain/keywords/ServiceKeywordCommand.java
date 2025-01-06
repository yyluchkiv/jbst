package jbst.ops.server.domain.keywords;

import jbst.ops.server.domain.authorities.Permission;
import jbst.ops.server.properties.atomics.Service;
import lombok.*;

import java.util.List;

// Lombok
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class ServiceKeywordCommand {
    private Service service;
    private KeywordCommand keywordCommand;
    private List<Permission> permissions;

    @Setter
    private String key;

    public static ServiceKeywordCommand of(
            Service service,
            KeywordCommand keywordCommand,
            List<Permission> permissions
    ) {
        var instance = new ServiceKeywordCommand();
        instance.service = service;
        instance.keywordCommand = keywordCommand;
        instance.permissions = permissions;
        return instance;
    }
}
