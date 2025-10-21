package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.base.ServerName;
import jbst.foundation.domain.base.Version;
import jbst.foundation.domain.properties.AbstractProperty;
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
public class ServerConfigs extends AbstractProperty {
    @MandatoryProperty
    private final ServerName name;
    @MandatoryProperty
    private final MavenConfigs mavenConfigs;
    @MandatoryProperty
    private final Boolean springdocEnabled;
    @NonMandatoryProperty
    private String serverURL;
    @NonMandatoryProperty
    private String webclientURL;

    public static ServerConfigs hardcoded() {
        return new ServerConfigs(
                ServerName.hardcoded(),
                new MavenConfigs("jbst", "jbst", Version.hardcoded()),
                true,
                "http://127.0.0.1:3002",
                "http://127.0.0.1:3000"
        );
    }

    @Override
    public boolean isParent() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonMandatory() {
        return "server-configs";
    }

    public boolean isSpringdocEnabled() {
        return this.springdocEnabled;
    }
}
