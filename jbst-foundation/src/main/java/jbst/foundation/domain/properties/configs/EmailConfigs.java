package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryToggleProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class EmailConfigs extends JbstProperty {
    @MandatoryProperty
    private final boolean enabled;
    @MandatoryToggleProperty
    private String host;
    @MandatoryToggleProperty
    private Integer port;
    @MandatoryToggleProperty
    private String from;
    @MandatoryToggleProperty
    private Username username;
    @MandatoryToggleProperty
    private Password password;

    public static EmailConfigs hardcoded() {
        return new EmailConfigs(
                true,
                "smtp.gmail.com",
                587,
                "jbst",
                Username.hardcoded(),
                Password.hardcoded()
        );
    }

    public static EmailConfigs disabled() {
        return new EmailConfigs(false, null, 0, null, null, null);
    }

    public static EmailConfigs enabled(String from) {
        return new EmailConfigs(true, "smtp.gmail.com", 587, from, Username.hardcoded(), Password.hardcoded());
    }

    @SuppressWarnings("unused")
    public static EmailConfigs gmail(String from, Username username, Password password) {
        return new EmailConfigs(true, "smtp.gmail.com", 587, from, username, password);
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.ROOT;
    }

    @Override
    public boolean isToggle() {
        return this.enabled;
    }

    @Override
    public String getNameNonLeaf() {
        return "email-configs";
    }
}
