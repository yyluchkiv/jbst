package jbst.server.hm.properties;

import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.configs.AbstractPropertiesConfigs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.utilities.random.RandomUtility.randomIPv4;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class ServerConfigs extends AbstractPropertiesConfigs {
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
    public boolean isParentPropertiesNode() {
        return true;
    }

    @Override
    public PropertyId getPropertyName() {
        return new PropertyId("server-configs");
    }
}
