package jbst.server.iam.base.properties;

import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.configs.AbstractPropertiesConfigs;
import jbst.server.iam.base.domain.enums.UserAuthority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class ServerConfigs extends AbstractPropertiesConfigs {
    @MandatoryProperty
    private final String targetAttribute1;
    @MandatoryProperty
    private final long targetAttribute2;
    @MandatoryProperty
    private final UserAuthority targetAuthority;

    @Override
    public boolean isParentPropertiesNode() {
        return true;
    }

    @Override
    public PropertyId getPropertyId() {
        return new PropertyId("server-configs");
    }
}
