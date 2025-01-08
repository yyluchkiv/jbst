package jbst.ops.server.properties.configs;

import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.AbstractPropertyConfigs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class GithubConfigs extends AbstractPropertyConfigs {
    @MandatoryProperty
    private final String token;
    @MandatoryProperty
    private final String owner;
    @MandatoryProperty
    private final String repo;
    @MandatoryProperty
    private final String content;
}
