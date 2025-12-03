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
public class JbstPropertyWebsocketsMessageBrokerRegistry extends JbstProperty {
    // INFO: spring support list of prefixes as varargs
    @JbstPropertyMandatory
    private final String applicationDestinationPrefix;
    // INFO: spring support list of destinations as varargs
    @JbstPropertyMandatory
    private final String simpleDestination;
    @JbstPropertyMandatory
    private final String userDestinationPrefix;

    public static JbstPropertyWebsocketsMessageBrokerRegistry hardcoded() {
        return new JbstPropertyWebsocketsMessageBrokerRegistry("/app", "/queue", "/user");
    }

    public static JbstPropertyWebsocketsMessageBrokerRegistry random() {
        return new JbstPropertyWebsocketsMessageBrokerRegistry(randomString(), randomString(), randomString());
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
