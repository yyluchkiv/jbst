package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.base.ServerName;
import jbst.foundation.domain.base.Version;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.NonMandatoryProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyServer extends JbstProperty {
    @MandatoryProperty
    private final ServerName name;
    @MandatoryProperty
    private final JbstPropertyMaven mavenConfigs;
    @MandatoryProperty
    private final Boolean springdocEnabled;
    @NonMandatoryProperty
    private String serverURL;
    @NonMandatoryProperty
    private String webclientURL;

    public static JbstPropertyServer hardcoded() {
        return new JbstPropertyServer(
                ServerName.hardcoded(),
                new JbstPropertyMaven("jbst", "jbst", Version.hardcoded()),
                true,
                "http://127.0.0.1:3002",
                "http://127.0.0.1:3000"
        );
    }

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
        return "server-configs";
    }

    public boolean isSpringdocEnabled() {
        return this.springdocEnabled;
    }
}
