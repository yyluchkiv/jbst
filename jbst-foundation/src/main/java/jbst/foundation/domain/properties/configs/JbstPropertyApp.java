package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.base.ServerName;
import jbst.foundation.domain.base.Version;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.properties.annotations.JbstPropertyOptional;
import jbst.foundation.domain.properties.configs.server.JbstPropertyMaven;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyApp extends JbstProperty {
    @JbstPropertyMandatory
    private final ServerName name;
    @JbstPropertyMandatory
    private final JbstPropertyMaven maven;
    @JbstPropertyMandatory
    private final Boolean springdocEnabled;
    @JbstPropertyOptional
    private String serverURL;
    @JbstPropertyOptional
    private String webclientURL;

    public static JbstPropertyApp fixed() {
        return new JbstPropertyApp(
                ServerName.fixed(),
                new JbstPropertyMaven("jbst", "jbst", Version.fixed()),
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
        return "app";
    }

    public boolean isSpringdocEnabled() {
        return this.springdocEnabled;
    }
}
