package jbst.server.hm.properties;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.utilities.random.RandomUtility.randomIPv4;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyServer extends JbstProperty {
    @JbstPropertyMandatory
    private final String targetURL;

    public static JbstPropertyServer hardcoded() {
        return new JbstPropertyServer(
                "http://localhost:8484"
        );
    }

    public static JbstPropertyServer random() {
        return new JbstPropertyServer(
                randomIPv4()
        );
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.ROOT;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonLeaf() {
        return "server";
    }
}
