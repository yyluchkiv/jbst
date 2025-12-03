package jbst.foundation.domain.properties.configs.security.websockets;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.domain.random.JbstRandom.randomString;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyWebsocketsStompEndpointRegistry extends JbstProperty {
    // Spring support list of endpoints as varargs
    @JbstPropertyMandatory
    private final String endpoint;

    public static JbstPropertyWebsocketsStompEndpointRegistry hardcoded() {
        return new JbstPropertyWebsocketsStompEndpointRegistry("/endpoint");
    }

    public static JbstPropertyWebsocketsStompEndpointRegistry random() {
        return new JbstPropertyWebsocketsStompEndpointRegistry(randomString());
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
