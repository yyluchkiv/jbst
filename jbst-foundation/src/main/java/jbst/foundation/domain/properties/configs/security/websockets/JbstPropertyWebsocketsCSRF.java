package jbst.foundation.domain.properties.configs.security.websockets;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.utilities.random.RandomUtility.randomString;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyWebsocketsCSRF extends JbstProperty {
    @JbstPropertyMandatory
    private final String headerName;
    @JbstPropertyMandatory
    private final String parameterName;
    @JbstPropertyMandatory
    private final String tokenKey;

    public static JbstPropertyWebsocketsCSRF hardcoded() {
        return new JbstPropertyWebsocketsCSRF("csrf-header", "_csrf", "csrf-token-key");
    }

    public static JbstPropertyWebsocketsCSRF random() {
        return new JbstPropertyWebsocketsCSRF(randomString(), randomString(), randomString());
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
}
