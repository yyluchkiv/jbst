package jbst.server.ops.properties.configs;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.NonMandatoryProperty;
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
public class ServersConfigs extends JbstProperty {
    @MandatoryProperty
    private final Mode mode;
    @MandatoryProperty
    private final String rsaKeysBaseLocation;
    @MandatoryProperty
    private final ServersMonitoringConfigs monitoringConfigs;
    @NonMandatoryProperty
    private final GithubConfigs githubConfigs;

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.ROOT;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonLeaf() {
        return "servers-configs";
    }

    public enum Mode {
        RESOURCES,
        GITHUB
    }
}
