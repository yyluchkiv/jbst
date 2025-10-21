package jbst.server.ops.properties.configs;

import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.NonMandatoryProperty;
import jbst.foundation.domain.properties.configs.AbstractPropertiesConfigs;
import jbst.server.ops.properties.base.GithubConfigs;
import jbst.server.ops.properties.base.ServersMonitoringConfigs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class ServersConfigs extends AbstractPropertiesConfigs {
    @MandatoryProperty
    private final Mode mode;
    @MandatoryProperty
    private final String rsaKeysBaseLocation;
    @MandatoryProperty
    private final ServersMonitoringConfigs monitoringConfigs;
    @NonMandatoryProperty
    private final GithubConfigs githubConfigs;

    @Override
    public boolean isParentPropertiesNode() {
        return true;
    }

    @Override
    public PropertyId getPropertyName() {
        return new PropertyId("servers-configs");
    }

    public enum Mode {
        RESOURCES,
        GITHUB
    }
}
