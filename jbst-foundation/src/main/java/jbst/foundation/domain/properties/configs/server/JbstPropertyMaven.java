package jbst.foundation.domain.properties.configs.server;

import jbst.foundation.domain.base.Version;
import jbst.foundation.domain.maven.MavenDetails;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyMaven extends JbstProperty {
    @MandatoryProperty
    private final String groupId;
    @MandatoryProperty
    private final String artifactId;
    @MandatoryProperty
    private final Version version;

    public static JbstPropertyMaven hardcoded() {
        return new JbstPropertyMaven("jbst", "jbst", Version.hardcoded());
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.BRANCH;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonLeaf() {
        return "maven-configs";
    }

    public MavenDetails asMavenDetails() {
        return new MavenDetails(this.groupId, this.artifactId, this.version);
    }
}
