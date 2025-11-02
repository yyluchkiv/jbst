package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryPropertyToggle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyEmail extends JbstProperty {
    @MandatoryProperty
    private final boolean enabled;
    @MandatoryPropertyToggle
    private String host;
    @MandatoryPropertyToggle
    private Integer port;
    @MandatoryPropertyToggle
    private String from;
    @MandatoryPropertyToggle
    private Username username;
    @MandatoryPropertyToggle
    private Password password;

    public static JbstPropertyEmail hardcoded() {
        return new JbstPropertyEmail(
                true,
                "smtp.gmail.com",
                587,
                "jbst",
                Username.hardcoded(),
                Password.hardcoded()
        );
    }

    public static JbstPropertyEmail disabled() {
        return new JbstPropertyEmail(false, null, 0, null, null, null);
    }

    public static JbstPropertyEmail enabled(String from) {
        return new JbstPropertyEmail(true, "smtp.gmail.com", 587, from, Username.hardcoded(), Password.hardcoded());
    }

    @SuppressWarnings("unused")
    public static JbstPropertyEmail gmail(String from, Username username, Password password) {
        return new JbstPropertyEmail(true, "smtp.gmail.com", 587, from, username, password);
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
