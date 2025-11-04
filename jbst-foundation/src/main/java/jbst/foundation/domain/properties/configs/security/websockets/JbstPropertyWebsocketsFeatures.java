package jbst.foundation.domain.properties.configs.security.websockets;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyOptional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyWebsocketsFeatures extends JbstProperty {
    @JbstPropertyOptional
    private JbstPropertyWebsocketsFeature hardwareConfigs;
    @JbstPropertyOptional
    private JbstPropertyWebsocketsFeature resetServerConfigs;

    public static JbstPropertyWebsocketsFeatures hardcoded() {
        return new JbstPropertyWebsocketsFeatures(
                JbstPropertyWebsocketsFeature.hardcoded(),
                JbstPropertyWebsocketsFeature.hardcoded()
        );
    }

    public static JbstPropertyWebsocketsFeatures random() {
        return new JbstPropertyWebsocketsFeatures(
                JbstPropertyWebsocketsFeature.random(),
                JbstPropertyWebsocketsFeature.random()
        );
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
        return "websockets-features-configs";
    }
}
