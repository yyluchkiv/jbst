package jbst.foundation.domain.properties.configs.security;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryPropertyToggle;
import jbst.foundation.domain.properties.configs.security.websockets.JbstPropertyWebsocketsCSRF;
import jbst.foundation.domain.properties.configs.security.websockets.JbstPropertyWebsocketsMessageBrokerRegistry;
import jbst.foundation.domain.properties.configs.security.websockets.JbstPropertyWebsocketsStompEndpointRegistry;
import jbst.foundation.domain.properties.configs.security.websockets.JbstPropertyWebsocketsFeatures;
import jbst.foundation.utilities.random.RandomUtility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertySecurityWebsockets extends JbstProperty {
    @MandatoryProperty
    private final boolean enabled;
    @MandatoryPropertyToggle
    private final JbstPropertyWebsocketsCSRF csrfConfigs;
    @MandatoryPropertyToggle
    private final JbstPropertyWebsocketsStompEndpointRegistry stompConfigs;
    @MandatoryPropertyToggle
    private final JbstPropertyWebsocketsMessageBrokerRegistry brokerConfigs;
    @MandatoryPropertyToggle
    private JbstPropertyWebsocketsFeatures featuresConfigs;

    public static JbstPropertySecurityWebsockets hardcoded() {
        return new JbstPropertySecurityWebsockets(
                true,
                JbstPropertyWebsocketsCSRF.hardcoded(),
                JbstPropertyWebsocketsStompEndpointRegistry.hardcoded(),
                JbstPropertyWebsocketsMessageBrokerRegistry.hardcoded(),
                JbstPropertyWebsocketsFeatures.hardcoded()
        );
    }

    public static JbstPropertySecurityWebsockets random() {
        return new JbstPropertySecurityWebsockets(
                RandomUtility.randomBoolean(),
                JbstPropertyWebsocketsCSRF.random(),
                JbstPropertyWebsocketsStompEndpointRegistry.random(),
                JbstPropertyWebsocketsMessageBrokerRegistry.random(),
                JbstPropertyWebsocketsFeatures.random()
        );
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.BRANCH;
    }

    @Override
    public boolean isToggle() {
        return this.enabled;
    }

    @Override
    public String getNameNonLeaf() {
        return "websockets-configs";
    }
}
