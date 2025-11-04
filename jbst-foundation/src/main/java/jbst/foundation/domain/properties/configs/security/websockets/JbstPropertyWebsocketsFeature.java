package jbst.foundation.domain.properties.configs.security.websockets;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.properties.annotations.MandatoryPropertyToggle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;
import static jbst.foundation.utilities.random.RandomUtility.randomString;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyWebsocketsFeature extends JbstProperty {
    @JbstPropertyMandatory
    private final boolean enabled;
    @MandatoryPropertyToggle
    private String userDestination;

    public static JbstPropertyWebsocketsFeature hardcoded() {
        return new JbstPropertyWebsocketsFeature(true, "/accounts");
    }

    public static JbstPropertyWebsocketsFeature random() {
        return new JbstPropertyWebsocketsFeature(randomBoolean(), randomString());
    }

    public static JbstPropertyWebsocketsFeature enabled() {
        return hardcoded();
    }

    public static JbstPropertyWebsocketsFeature disabled() {
        return new JbstPropertyWebsocketsFeature(false, null);
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.LEAF;
    }

    @Override
    public boolean isToggle() {
        return this.enabled;
    }

    @Override
    public String getNameNonLeaf() {
        return JbstConstants.Symbols.DASH;
    }
}
