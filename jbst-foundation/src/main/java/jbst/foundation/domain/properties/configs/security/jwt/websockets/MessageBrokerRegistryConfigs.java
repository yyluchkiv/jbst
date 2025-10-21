package jbst.foundation.domain.properties.configs.security.jwt.websockets;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.utilities.random.RandomUtility.randomString;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class MessageBrokerRegistryConfigs extends JbstProperty {
    // INFO: spring support list of prefixes as varargs
    @MandatoryProperty
    private final String applicationDestinationPrefix;
    // INFO: spring support list of destinations as varargs
    @MandatoryProperty
    private final String simpleDestination;
    @MandatoryProperty
    private final String userDestinationPrefix;

    public static MessageBrokerRegistryConfigs hardcoded() {
        return new MessageBrokerRegistryConfigs("/app", "/queue", "/user");
    }

    public static MessageBrokerRegistryConfigs random() {
        return new MessageBrokerRegistryConfigs(randomString(), randomString(), randomString());
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonMandatory() {
        return JbstConstants.Symbols.DASH;
    }
}
