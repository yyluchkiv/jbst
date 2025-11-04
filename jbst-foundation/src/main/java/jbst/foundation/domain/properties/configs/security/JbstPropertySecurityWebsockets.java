package jbst.foundation.domain.properties.configs.security;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatoryOnToggleEnabled;
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
    @JbstPropertyMandatory
    private final boolean enabled;
    @JbstPropertyMandatoryOnToggleEnabled
    private final JbstPropertyWebsocketsCSRF csrfConfigs;
    @JbstPropertyMandatoryOnToggleEnabled
    private final JbstPropertyWebsocketsStompEndpointRegistry stompConfigs;
    @JbstPropertyMandatoryOnToggleEnabled
    private final JbstPropertyWebsocketsMessageBrokerRegistry brokerConfigs;
    @JbstPropertyMandatoryOnToggleEnabled
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
