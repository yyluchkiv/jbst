package jbst.server.ops.properties.configs;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.properties.annotations.JbstPropertyOptional;
import jbst.server.ops.properties.base.JbstPropertyOpsGithub;
import jbst.server.ops.properties.base.JbstPropertyOpsServersMonitoring;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyOpsServers extends JbstProperty {
    @JbstPropertyMandatory
    private final Mode mode;
    @JbstPropertyMandatory
    private final String rsaKeysBaseLocation;
    @JbstPropertyMandatory
    private final JbstPropertyOpsServersMonitoring monitoring;
    @JbstPropertyOptional
    private final JbstPropertyOpsGithub github;

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
        return "servers";
    }

    public enum Mode {
        RESOURCES,
        GITHUB
    }
}
