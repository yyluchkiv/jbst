package jbst.foundation.domain.properties.configs.security;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryPropertyToggle;
import jbst.foundation.domain.properties.configs.security.websockets.JbstPropertyCSRF;
import jbst.foundation.domain.properties.configs.security.websockets.JbstPropertyMessageBrokerRegistry;
import jbst.foundation.domain.properties.configs.security.websockets.JbstPropertyStompEndpointRegistry;
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
public class WebsocketsConfigs extends JbstProperty {
    @MandatoryProperty
    private final boolean enabled;
    @MandatoryPropertyToggle
    private final JbstPropertyCSRF csrfConfigs;
    @MandatoryPropertyToggle
    private final JbstPropertyStompEndpointRegistry stompConfigs;
    @MandatoryPropertyToggle
    private final JbstPropertyMessageBrokerRegistry brokerConfigs;
    @MandatoryPropertyToggle
    private JbstPropertyWebsocketsFeatures featuresConfigs;

    public static WebsocketsConfigs hardcoded() {
        return new WebsocketsConfigs(
                true,
                JbstPropertyCSRF.hardcoded(),
                JbstPropertyStompEndpointRegistry.hardcoded(),
                JbstPropertyMessageBrokerRegistry.hardcoded(),
                JbstPropertyWebsocketsFeatures.hardcoded()
        );
    }

    public static WebsocketsConfigs random() {
        return new WebsocketsConfigs(
                RandomUtility.randomBoolean(),
                JbstPropertyCSRF.random(),
                JbstPropertyStompEndpointRegistry.random(),
                JbstPropertyMessageBrokerRegistry.random(),
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
