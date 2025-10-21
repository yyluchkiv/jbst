package jbst.server.hm.properties;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.utilities.random.RandomUtility.randomIPv4;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class ServerConfigs extends JbstProperty {
    @MandatoryProperty
    private final String targetURL;

    public static ServerConfigs hardcoded() {
        return new ServerConfigs(
                "http://localhost:8484"
        );
    }

    public static ServerConfigs random() {
        return new ServerConfigs(
                randomIPv4()
        );
    }

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonMandatory() {
        return "server-configs";
    }
}
