package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.properties.annotations.JbstPropertyOptional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static java.util.Objects.nonNull;
import static jbst.foundation.utilities.random.RandomUtility.*;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyMongo extends JbstProperty {
    @JbstPropertyMandatory
    private final String host;
    @JbstPropertyMandatory
    private final Integer port;
    @JbstPropertyMandatory
    private final String name;
    @JbstPropertyOptional
    private Username username;
    @JbstPropertyOptional
    private Password password;

    public static JbstPropertyMongo hardcoded() {
        return JbstPropertyMongo.noSecurity("127.0.0.1", 27017, "jbst");
    }

    public static JbstPropertyMongo random() {
        return JbstPropertyMongo.noSecurity(randomIPv4(), randomIntegerGreaterThanZeroByBounds(26000, 30000), randomString());
    }

    public static JbstPropertyMongo noSecurity(String host, int port, String database) {
        return new JbstPropertyMongo(host, port, database, null, null);
    }

    public final String connectionString() {
        if (isAuthenticationRequired()) {
            return "mongodb://" + this.username.value() + ":" + this.password.value() + "@" + this.host + ":" + this.port + "/" + this.name;
        } else {
            return "mongodb://" + this.host + ":" + this.port + "/" + this.name;
        }
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.LEAF;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonLeaf() {
        return JbstConstants.Symbols.DASH;
    }

    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    private boolean isAuthenticationRequired() {
        return nonNull(this.username) && nonNull(this.password);
    }
}
