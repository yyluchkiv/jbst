package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatoryOnToggleEnabled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyEmails extends JbstProperty {
    @JbstPropertyMandatory
    private final boolean enabled;
    @JbstPropertyMandatoryOnToggleEnabled
    private String host;
    @JbstPropertyMandatoryOnToggleEnabled
    private Integer port;
    @JbstPropertyMandatoryOnToggleEnabled
    private String from;
    @JbstPropertyMandatoryOnToggleEnabled
    private Username username;
    @JbstPropertyMandatoryOnToggleEnabled
    private Password password;

    public static JbstPropertyEmails hardcoded() {
        return new JbstPropertyEmails(
                true,
                "smtp.gmail.com",
                587,
                "jbst",
                Username.hardcoded(),
                Password.hardcoded()
        );
    }

    public static JbstPropertyEmails disabled() {
        return new JbstPropertyEmails(false, null, 0, null, null, null);
    }

    public static JbstPropertyEmails enabled(String from) {
        return new JbstPropertyEmails(true, "smtp.gmail.com", 587, from, Username.hardcoded(), Password.hardcoded());
    }

    @SuppressWarnings("unused")
    public static JbstPropertyEmails gmail(String from, Username username, Password password) {
        return new JbstPropertyEmails(true, "smtp.gmail.com", 587, from, username, password);
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
